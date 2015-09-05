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
package com.threewks.thundr.user.jpa.authentication;

import com.threewks.thundr.jpa.repository.LongRepository;
import com.threewks.thundr.user.authentication.BasePasswordAuthentication;
import com.threewks.thundr.user.jpa.User;

import javax.persistence.*;
import java.util.List;

import static com.threewks.thundr.user.authentication.PasswordAuthenticationHelper.hash;
import static com.threewks.thundr.user.authentication.PasswordAuthenticationHelper.salt;

@Entity
@Table(name="authentication")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class PasswordAuthentication  implements JpaAuthentication<PasswordAuthentication> {

    public static class Fields {
        public final static String Id = "id";
        public final static String Username = "username";

        private Fields() {}
    }

    @Id
    @Column(name = "authentication_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    protected String username;

    @Column(name = "password")
    protected String hashedpassword;

    @Column(name = "salt")
    protected byte[] salt;

    @Column(name = "iterations")
    protected int iterations;

    @Column(name = "digest")
    protected String digest;

    /**
     * Default constructor for JPA.
     */
    public PasswordAuthentication() {
    }

    public PasswordAuthentication(String username, String password) {
        this(username, password, 1000, BasePasswordAuthentication.Digests.SHA512);
    }

    public PasswordAuthentication(String username, String password, int iterations, String digest) {
        this.username = username;
        this.iterations = iterations;
        this.digest = digest;
        this.salt = salt(8);
        this.hashedpassword = hash(password, salt, iterations, digest);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedpassword;
    }

    public byte[] getSalt() {
        return salt;
    }

    public String getDigest() {
        return digest;
    }

    public int getIterations() {
        return iterations;
    }

    @Override
    public void setUser(User user) {
        this.username = user.getUsername();
    }

    @Override
    public User getUser(LongRepository<User> userRepository) {
        List<User> users = userRepository.find(User.Fields.Username, username, 1);
        return users.size() > 0 ? users.get(0) : null;
    }

    @Override
    public PasswordAuthentication getMatchingAuthentication(LongRepository<PasswordAuthentication> passwordAuthenticationRepository, PasswordAuthentication authentication) {
        List<PasswordAuthentication> authentications = passwordAuthenticationRepository.find(Fields.Username, authentication.getUsername(), 1);
        return authentications.size() > 0 ? authentications.get(0) : null;
    }

    @Override
    public boolean validates(String authorisation) {
        return hashedpassword.equals(hash(authorisation, salt, iterations, digest));
    }

}
