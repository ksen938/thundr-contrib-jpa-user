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

import javax.persistence.*;
import java.security.SecureRandom;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

@Entity
@Table(name = "user_token")
public class UserToken {

    public static class Fields {
        public final static String Id = "id";
        public final static String User = "user";
        public final static String Token = "token";

        private Fields() {}
    }

    private static final String TOKEN_FORMAT = "%s:%s:%s";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Id
    @Column(name = "user_token_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "token")
    private String token;

    UserToken() {
    }

    public UserToken(User user) {
        this.user = user;
        this.token = generateToken();
    }

    Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    private String generateToken() {
        return sha1Hex(String.format(TOKEN_FORMAT, user.getId(), id, SECURE_RANDOM.nextLong()));
    }
}
