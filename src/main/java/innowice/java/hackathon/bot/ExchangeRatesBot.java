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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling
public class ExchangeRatesBot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);
    private static final String START = "/start";
    private static final String BITCOIN = "/bitcoin";
    private static final String HELP = "/help";
    private static final String PUSH_MESSAGE = "/push_message";
    private static final String UP = "/up";
    private static final String UP_3 = "/up_3";
    private static final String UP_5 = "/up_5";
    private static final String UP_10 = "/up_10";
    private static final String UP_15 = "/up_15";
    private static final String DOWN = "/down";
    private static final String DOWN_3 = "/down_3";
    private static final String DOWN_5 = "/down_5";
    private static final String DOWN_10 = "/down_10";
    private static final String DOWN_15 = "/down_15";

    @Autowired
    private RateService rateService;

    @Autowired
    private UserService userService;

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String massage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        switch (massage){
            case START -> {
                String userName = update.getMessage().getFrom().getUserName();
                startCommand(chatId, userName);
            }
            case BITCOIN -> bitcoinCommand(chatId);
            case PUSH_MESSAGE -> pushMessageCommand(chatId);
            case HELP -> helpCommand(chatId);
            case UP -> upCommand(chatId);
            case UP_3 -> upCommand_3(chatId);
            /*case UP_5 -> upCommand_5(chatId);
            case UP_10 -> upCommand_10(chatId);
            case UP_15 -> upCommand_15(chatId);*/
            case DOWN -> downCommand(chatId);
            case DOWN_3 -> downCommand_3(chatId);
            /*case DOWN_5 -> downCommand_5(chatId);
            case DOWN_10 -> downCommand_10(chatId);
            case DOWN_15 -> downCommand_15(chatId);*/
            default -> unnoundCommand(chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return "innowice_java_hackathon_bot";
    }

    private void startCommand(Long chatId, String userName) {
        User user = userService.findByUserName(userName);

        if (user == null) {
            userService.saveUser(chatId, userName);
        }

        var text = """
                Добро пожаловать в бот, %s!
                
                Здесь Вы сможете узнать курс валют для BITCOIN на сегодня и узнать о поовышении или понижении валюты.
                
                Для этого воспользуйтесь командами:
                /bitcoin - узнать текущий курс BITCOIN
                /push_message - получить уведомление о изменении курса
                
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
            String formattedDateTime = formatDateTime(now);

            String bitcoin = rateService.getBitcoinExchangeRate();
            String text = "Курс Bitcoin на %s составляет %s bitcoin";
            formattedText = String.format(text, formattedDateTime, bitcoin);

            ExchangeRate exchangeRate = rateService.findByChartId(chatId);

            if (exchangeRate == null) {
                rateService.saveExchangeRate(chatId, bitcoin, formattedDateTime);
            } else {
                exchangeRate.setPrice(bitcoin);
                exchangeRate.setDate(formattedDateTime);
                rateService.save(exchangeRate);
            }

        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса Bitcoin", e);
            formattedText = "Не удалось получить курс Bitcoin, попробуйте позже";
        }
        sendMessage(chatId, formattedText);
    }
    @Scheduled(fixedRate = 20000)
    public void updateBitcoinExchangeRate() {
        try {
            Long chatId = 643865332L;
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = formatDateTime(now);

            String bitcoin = rateService.getBitcoinExchangeRate();
            ExchangeRate exchangeRate = rateService.findByChartId(chatId);

            if (exchangeRate == null) {
                rateService.saveExchangeRate(chatId, bitcoin, formattedDateTime);
            } else {
                exchangeRate.setPrice(bitcoin);
                exchangeRate.setDate(formattedDateTime);
                rateService.save(exchangeRate);
            }

            // Логирование успешного обновления данных
            LOG.info("Данные Bitcoin обновлены: курс {}, время обновления {}", bitcoin, formattedDateTime);

        } catch (ServiceException e) {
            LOG.error("Ошибка обновления курса Bitcoin", e);
        }
    }
    private void pushMessageCommand(Long chatId) {
        var text = """
                Для получения уведомления о изменении курса валюты воспользуйтесь одной из следующих команд:
                
                /up - получить уведомление о повышении курса
                /down - получить уведомление о понижении курса
                
                Дополнительные команды:
                /help - получение справки
                """;
        sendMessage(chatId, text);
    }

    private void upCommand(Long chatId) {
        var text = """
                Выберете команду при повышении на сколько процентов вы хотите получить уведомление:
                
                /up_3 - получить уведомление при повышении на 3%
                /up_5 - получить уведомление при повышении на 5%
                /up_10 - получить уведомление при повышении на 10%
                /up_15 - получить уведомление при повышении на 15%
                """;
        sendMessage(chatId, text);
    }

    private void downCommand(Long chatId) {
        var text = """
                Выберете команду при повышении на сколько процентов вы хотите получить уведомление:
                
                /down_3 - получить уведомление при повышении на 3%
                /down_5 - получить уведомление при повышении на 5%
                /down_10 - получить уведомление при повышении на 10%
                /down_15 - получить уведомление при повышении на 15%
                """;
        sendMessage(chatId, text);
    }

    private void upCommand_3(Long chatId) {
        var text = """
                test push message for up bitcoin for 3%
                """;
        sendMessage(chatId, text);
    }

    private void downCommand_3(Long chatId) {
        var text = """
                test push message for down bitcoin for 3%
                """;
        sendMessage(chatId, text);
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
