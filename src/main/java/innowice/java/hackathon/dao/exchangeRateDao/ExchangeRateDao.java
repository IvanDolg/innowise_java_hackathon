package innowice.java.hackathon.dao.exchangeRateDao;

import innowice.java.hackathon.dao.Dao;
import innowice.java.hackathon.entity.ExchangeRate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ExchangeRateDao implements Dao<ExchangeRate, Long> {

    private final SessionFactory sessionFactory;

    public ExchangeRateDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        Session session = sessionFactory.openSession();
        session.save(exchangeRate);
        session.close();
        return exchangeRate;
    }
}
