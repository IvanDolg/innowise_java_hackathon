package innowice.java.hackathon.service.impl;

import com.google.gson.Gson;
import innowice.java.hackathon.client.CbrClient;
import innowice.java.hackathon.entity.ExchangeRate;
import innowice.java.hackathon.exception.ServiceException;
import innowice.java.hackathon.repository.ExchangeRateRepository;
import innowice.java.hackathon.service.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {
    @Autowired
    private CbrClient cbrClient;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Override
    public String getBitcoinExchangeRate() throws ServiceException {
        var xml = cbrClient.getCurrencyRatesXML();
        return parseJsonToString(xml);
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        return exchangeRateRepository.save(exchangeRate);
    }

    public String parseJsonToString(String json) {
        Gson gson = new Gson();
        ExchangeRate exchangeRate = gson.fromJson(json, ExchangeRate.class);
        return exchangeRate.getFormattedText();
    }
}
