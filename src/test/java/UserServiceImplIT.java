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
import com.threewks.thundr.injection.InjectionContextImpl;
import com.threewks.thundr.jpa.Action;
import com.threewks.thundr.jpa.Jpa;
import com.threewks.thundr.jpa.ResultAction;
import com.threewks.thundr.jpa.repository.LongRepository;
import com.threewks.thundr.user.InMemorySessionStore;
import com.threewks.thundr.user.SessionStore;
import com.threewks.thundr.user.authentication.Authentication;
import com.threewks.thundr.user.jpa.*;
import com.threewks.thundr.user.jpa.authentication.PasswordAuthentication;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import rule.ConfigureHibernate;
import rule.ConfigureHikari;
import rule.ConfigureHsql;
import rule.ConfigureMysql;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by kaushiksen on 4/09/2015.
 */
public class UserServiceImplIT {

    public InjectionContextImpl injectionContext = new InjectionContextImpl();

    public ConfigureHsql configureHsql = new ConfigureHsql(injectionContext);
    public ConfigureMysql configureMysql = new ConfigureMysql(injectionContext);
    public ConfigureHikari configureHikari = new ConfigureHikari(injectionContext);
    public ConfigureHibernate configureHibernate = new ConfigureHibernate(injectionContext, User.class, PasswordAuthentication.class, UserToken.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public RuleChain chain = RuleChain.outerRule(configureMysql).around(configureHikari).around(configureHibernate);

    private User actualUser1;
    private User actualUser2;
    private User actualUser3;
    private Jpa jpa;
    private UserRepositoryImpl<User> userRepository;
    private UserTokenRepositoryImpl userTokenRepository;
    private LongRepository<UserToken> userTokenLongRepository; //used to query the token repository directly

    private UserServiceImpl userService;

    private SessionStore sessionStore = new InMemorySessionStore();
    private PasswordAuthentication user1Authentication;
    private PasswordAuthentication user2Authentication;

    @Before
    public void before() {
        actualUser1 = new User();
        actualUser1.setEmail("a@a.com");
        actualUser1.setUsername("actualUser1");
        actualUser1.setLastLogin(DateTime.now());
        actualUser1.addRole("ADMINISTRATOR");
        actualUser1.setProperty("name1", "value1");

        actualUser2 = new User();
        actualUser2.setEmail("b@b.com");
        actualUser2.setUsername("actualUser2");
        actualUser2.setLastLogin(DateTime.now());
        actualUser2.addRole("CUSTOMER");
        actualUser2.setProperty("name2", "value2");

        actualUser3 = new User();
        actualUser3.setEmail("a@a.com");
        actualUser3.setUsername("actualUser3");
        actualUser3.setLastLogin(DateTime.now());
        actualUser3.addRole("ADMINISTRATOR");
        actualUser3.setProperty("name1", "value1");

        jpa = injectionContext.get(Jpa.class);

        userRepository = new UserRepositoryImpl<>(User.class, jpa);

        userTokenRepository = new UserTokenRepositoryImpl(jpa);
        userTokenLongRepository = new LongRepository<>(UserToken.class, jpa);

        userRepository.create(actualUser1);
        userRepository.create(actualUser2);

        userService = new UserServiceImpl(userTokenRepository, userRepository);

        user1Authentication = new PasswordAuthentication(actualUser1.getUsername(), "password1");
        user2Authentication = new PasswordAuthentication(actualUser2.getUsername(), "password2");

        userRepository.putAuthentication(actualUser1, user1Authentication);

    }

    @Test
    public void shouldCreateUser() {
        userService.create(actualUser3);

        User expectedUser = userService.read(actualUser3.getUsername());
        assertUsersEqual(expectedUser, actualUser3);
    }

    @Test
    public void shouldFailToCreateDuplicateUser() {
        thrown.expect(PersistenceException.class);

        User duplicateUser = new User();
        duplicateUser.setUsername(actualUser1.getUsername());
        userService.create(duplicateUser);
    }

    @Test
    public void shouldReadUser() {
        User expectedUser = userService.read(actualUser1.getUsername());
        assertUsersEqual(expectedUser, actualUser1);
    }

    @Test
    public void shouldUpdateUser() {
        actualUser1.setUsername("CHANGED");
        userService.update(actualUser1);
        User expectedUser = userService.read("CHANGED");
        assertThat(expectedUser.getUsername(), is("CHANGED"));
    }

    @Test
    public void shouldDeleteUser() {
        String username = actualUser1.getUsername();
        userService.delete(actualUser1);
        User expectedUser = userService.read(username);
        assertThat(expectedUser, is(nullValue()));
    }

    @Test
    public void shouldDeleteUserByUsername() {
        String username = actualUser1.getUsername();
        userService.delete(username);
        User expectedUser = userService.read(username);
        assertThat(expectedUser, is(nullValue()));
    }

    @Test
    public void shouldFindUserByAttribute() {
        String actualEmail = actualUser1.getEmail();
        User expectedUser = userService.find("email", actualEmail, 1).get(0);
        assertUsersEqual(expectedUser, actualUser1);
    }

    @Test
    public void shouldFindUserByMultipleAttributes() {
        userService.create(actualUser3);

        String actualUser1Email = actualUser1.getEmail();
        String actualUser3Email = actualUser3.getEmail();
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("email", actualUser1Email);
        List<User> users = userService.find(criteria, 10);
        assertThat(users.size(), is(2));

        criteria.clear();
        criteria.put("email", actualUser3Email);
        criteria.put("username", actualUser3.getUsername());
        User expectedUser = userService.find(criteria, 10).get(0);
        assertUsersEqual(expectedUser, actualUser3);
    }

    @Test
    public void shouldPutAndGetAuthentication() {
        User putUser = userService.put(actualUser2, user2Authentication);
        User expectedUser = userService.get(user2Authentication);
        assertUsersEqual(expectedUser, actualUser2);
    }

    @Test
    public void shouldLoginUser() {
        User loggedInUser = userService.login(user1Authentication, "password1", sessionStore);
        assertUsersEqual(loggedInUser, actualUser1);
    }

    @Test
    public void shouldFailLoginWithIncorrectPassword() {
        User loggedInUser = userService.login(user1Authentication, "WRONG", sessionStore);
        assertThat(loggedInUser, is(nullValue()));
    }

    @Test
    public void shouldFailLoginWithIncorrectUsername() {
        PasswordAuthentication wrongAuth = new PasswordAuthentication("WRONG", "password1");
        User loggedInUser = userService.login(wrongAuth, "password1", sessionStore);
        assertThat(loggedInUser, is(nullValue()));
    }

    @Test
    public void shouldExpireTokenWhenLoggedOut() {
        userService.login(user1Authentication, "password1", sessionStore);
        String token = sessionStore.get();
        User loggedInUser = userTokenRepository.getUserForToken(token);

        assertUsersEqual(loggedInUser, actualUser1);

        userService.logout(loggedInUser);
        User expectedUser = userTokenRepository.getUserForToken(token);

        assertThat(expectedUser, is(nullValue()));
    }

    @Test
    public void shouldCreateTokensAndExpireOne() {
        userService.login(user1Authentication, "password1", sessionStore);
        String token = sessionStore.get();
        jpa.run(new Action() {
            @Override
            public void run(EntityManager em) {
                List<UserToken> tokens = userTokenLongRepository.find("user", 1, 10);
                assertThat(tokens.size(), is(2));
            }
        });
        userService.logout(actualUser1, sessionStore);
        jpa.run(new Action() {
            @Override
            public void run(EntityManager em) {
                assertThat(userTokenLongRepository.count(), is(1l));

                User expectedUser = userTokenRepository.getUserForToken(token);
                assertThat(expectedUser, is(nullValue()));
            }
        });
    }

    @Test
    public void shouldCreateTokensAndExpireAllOnDelete() {
        userService.login(user1Authentication, "password1", sessionStore);
        userService.login(user1Authentication, "password1", sessionStore);
        jpa.run(new Action() {
            @Override
            public void run(EntityManager em) {
                List<UserToken> tokens = userTokenLongRepository.find("user", 1, 10);
                assertThat(tokens.size(), is(4));
            }
        });
        userService.delete(actualUser1);
        jpa.run(new Action() {
            @Override
            public void run(EntityManager em) {
                assertThat(userTokenLongRepository.count(), is(0l));
            }
        });
    }

    @Test
    public void shouldGetUserFromSession() {
        userService.login(user1Authentication, "password1", sessionStore);
        User expectedUser = userService.getUser(sessionStore);
        assertUsersEqual(expectedUser, actualUser1);
    }

    private void assertUsersEqual(User expectedUser, User actualUser)
    {
        assertThat(expectedUser.getId(), is(actualUser.getId()));
        assertThat(expectedUser.getEmail(), is(actualUser.getEmail()));
        assertThat(expectedUser.getRoles(), is(actualUser.getRoles()));
        assertThat(expectedUser.getProperties(), is(actualUser.getProperties()));
    }
}
