package com.threewks.thundr.user.jpa;

import com.threewks.thundr.user.BaseUserService;
import com.threewks.thundr.user.UserTokenRepository;

import java.util.List;
import java.util.Map;

public class UserServiceImpl extends BaseUserService<User> implements UserService {

    public UserServiceImpl(UserTokenRepository<User> tokenRepository, UserRepositoryImpl<User> userRepository) {
        super(tokenRepository, userRepository);
    }

    public User get(String username) {
        return userRepository().get(username);
    }

    public User put(User user) {
        userRepository.update(user);
        return user;
    }

    public void delete(String username) {
        delete(get(username));
    }

    public void delete(User user) {
        userRepository().delete(user);
        tokenRepository.expireTokens(user);
    }

    @Override
    public List<User> find(String key, Object value, int limit) {
        return userRepository().find(key, value, limit);
    }

    @Override
    public List<User> find(Map<String, Object> properties, int limit) {
        return userRepository().find(properties, limit);
    }

    private UserRepositoryImpl<User> userRepository() {
        return ((UserRepositoryImpl<User>) userRepository);
    }
}
