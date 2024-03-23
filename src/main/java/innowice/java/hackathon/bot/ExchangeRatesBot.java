package innowice.java.hackathon.bot;

import innowice.java.hackathon.entity.ExchangeRate;
import innowice.java.hackathon.entity.User;
import innowice.java.hackathon.exception.ServiceException;
import innowice.java.hackathon.service.RateService;
import innowice.java.hackathon.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);
    private static final String START = "/start";
    private static final String BITCOIN = "/bitcoin";
    private static final String HELP = "/help";

    @Value("${cbr.currency.rates.symbol}")
    private String symbol;

    @Autowired
    private RateService rateService;

    @Autowired
    private UserService userService;

    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var massage = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        switch (massage){
            case START -> {
                String userName = update.getMessage().getFrom().getUserName();

                // save
                User user = new User();
                user.setUserName(userName);
                userService.save(user);

                startCommand(chatId, userName);
            }
            case BITCOIN -> bitcoinCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unnoundCommand(chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return "innowice_java_hackathon_bot";
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                
                Здесь Вы сможете узнать официальный курс валют для BITCOIN на сегодня.
                
                Для этого воспользуйтесь командами:
                /bitcoin - узнать курс BITCOIN
                
                Дополнительные команды:
                /help - получение справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void bitcoinCommand(Long chatId) {
        String formattedText;
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            String bitcoin = rateService.getBitcoinExchangeRate();
            String text = "Курс Bitcoin на %s составляет %s bitcoin";
            formattedText = String.format(text, formattedDateTime, bitcoin);

            // save
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setSymbol("BTCUSDT");
            exchangeRate.setPrice(bitcoin);
            exchangeRate.setDate(formattedDateTime);
            rateService.save(exchangeRate);

        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса Bitcoin", e);
            formattedText = "Не удалось получить курс Bitcoin, попробуйте позже";
        }
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        String text =  """
                Справочная информация по боту
                
                Для получения текущего курса валют для BITCOIN воспользуйтесь командой:
                
                /bitcoin - курс BITCOIN
                """;
        sendMessage(chatId, text);
    }
    private void unnoundCommand(Long chatId) {
        String text = "Неизвестная команда";
        sendMessage(chatId, text);
    }
    private void sendMessage(Long chatId, String text) {
        var catIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(catIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки сообщения", e);
        }
    }
}
