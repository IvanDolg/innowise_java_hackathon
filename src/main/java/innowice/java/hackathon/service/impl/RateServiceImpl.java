package innowice.java.hackathon.service.impl;

import com.google.gson.Gson;
import innowice.java.hackathon.client.CbrClient;
import innowice.java.hackathon.entity.ExchangeRate;
import innowice.java.hackathon.exception.ServiceException;
import innowice.java.hackathon.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateServiceImpl implements RateService {
    @Autowired
    private CbrClient cbrClient;

    @Override
    public String getBitcoinExchangeRate() throws ServiceException {
        var xml = cbrClient.getCurrencyRatesXML();
        return parseJsonToString(xml);
    }
    public String parseJsonToString(String json) {
        Gson gson = new Gson();
        ExchangeRate exchangeRate = gson.fromJson(json, ExchangeRate.class);
        return exchangeRate.getFormattedText();
    }
}
