package innowice.java.hackathon.dao.userDao;

import innowice.java.hackathon.dao.Dao;
import innowice.java.hackathon.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class UserDao implements Dao<User, Long> {
    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User save(User user) {
        Session session = sessionFactory.openSession();
        session.save(user);
        session.close();
        return user;
    }
}
