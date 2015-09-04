package com.threewks.thundr.user.jpa;

import com.threewks.thundr.user.ThundrUserService;

import java.util.List;
import java.util.Map;


public interface UserService extends ThundrUserService<User> {

    public User get(String username);

    public User put(User user);

    public void delete(String username);

    public List<User> find(String key, Object value, int limit);

    public List<User> find(Map<String, Object> properties, int limit);

}
