package innowice.java.hackathon.service;

import innowice.java.hackathon.exception.ServiceException;

public interface RateService {
    String getBitcoinExchangeRate () throws ServiceException;
}
