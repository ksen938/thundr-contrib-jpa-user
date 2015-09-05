/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://www.3wks.com.au/thundr
 * Copyright (C) 2013 3wks, <thundr@3wks.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.threewks.thundr.user.jpa;

import com.threewks.thundr.user.BaseUserService;
import com.threewks.thundr.user.UserTokenRepository;

import java.util.List;
import java.util.Map;

public class UserServiceImpl extends BaseUserService<User> implements UserService {

    public UserServiceImpl(UserTokenRepository<User> tokenRepository, UserRepositoryImpl<User> userRepository) {
        super(tokenRepository, userRepository);
    }

    public User read(String username) {
        return userRepository().get(username);
    }

    public User create(User user) {
        return userRepository().create(user);
    }

    public User update(User user) {
        userRepository.update(user);
        return user;
    }

    public void delete(String username) {
        delete(read(username));
    }

    public void delete(User user) {
        tokenRepository.expireTokens(user);
        userRepository().delete(user);
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
