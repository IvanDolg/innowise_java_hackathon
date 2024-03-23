package innowice.java.hackathon.service.impl;

import innowice.java.hackathon.dao.userDao.UserDao;
import innowice.java.hackathon.entity.User;
import innowice.java.hackathon.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }
    @Override
    public User save(User user) {
        return userDao.save(user);
    }
}
