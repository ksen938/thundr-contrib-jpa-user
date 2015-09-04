package com.threewks.thundr.user.jpa;

import com.atomicleopard.expressive.Cast;
import com.threewks.thundr.jpa.Action;
import com.threewks.thundr.jpa.Jpa;
import com.threewks.thundr.jpa.Propagation;
import com.threewks.thundr.jpa.ResultAction;
import com.threewks.thundr.jpa.repository.LongRepository;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserServiceException;
import com.threewks.thundr.user.authentication.Authentication;
import com.threewks.thundr.user.jpa.authentication.PasswordAuthentication;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

public class UserRepositoryImpl<U extends User> implements UserRepository<U> {

    private static final int LIMIT_ONE = 1;

    private final LongRepository<U> userRepository;
    private final LongRepository<PasswordAuthentication> passwordAuthenticationRepository;
    private Jpa jpa;

    public UserRepositoryImpl(Class<U> entityType, Jpa jpa) {
        this.jpa = jpa;
        userRepository = new LongRepository<>(entityType, jpa);
        passwordAuthenticationRepository = new LongRepository<>(PasswordAuthentication.class, jpa);
    }

    @Override
    public U putAuthentication(U user, Authentication authentication) {
        final PasswordAuthentication passwordAuthentication = passwordAuthentication(authentication);
        passwordAuthentication.setUser(user);

        update(user);

        jpa.run(Propagation.Required, new Action() {
            @Override
            public void run(EntityManager em) {
                PasswordAuthentication matchingAuthentication = passwordAuthentication.getMatchingAuthentication(passwordAuthenticationRepository, passwordAuthentication);
                if (matchingAuthentication != null) {
                    passwordAuthenticationRepository.delete(matchingAuthentication);
                    em.flush();
                }
                passwordAuthenticationRepository.create(passwordAuthentication);
            }
        });

        return user;
    }

    @Override
    public void removeAuthentication(Authentication authentication) {
        final PasswordAuthentication passwordAuthentication = passwordAuthentication(authentication);
        jpa.run(Propagation.Required, new Action() {
            @Override
            public void run(EntityManager em) {
                passwordAuthenticationRepository.delete(passwordAuthentication);
            }
        });
    }

    @Override
    public Authentication getAuthentication(Authentication authentication) {
        final PasswordAuthentication passwordAuthentication = passwordAuthentication(authentication);
        return jpa.run(Propagation.Required, new ResultAction<Authentication>() {
            @Override
            public Authentication run(EntityManager em) {
                return passwordAuthentication.getMatchingAuthentication(passwordAuthenticationRepository, passwordAuthentication);
            }
        });
    }

    @Override
    public void update(final U user) {
        jpa.run(Propagation.Required, new Action() {
            @Override
            public void run(EntityManager em) {
                List<U> users = userRepository.find(User.Fields.Username, user.getUsername(), LIMIT_ONE);
                if (users.isEmpty()) {
                    userRepository.create(user);
                } else {
                    userRepository.update(users.get(0));
                }
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public U get(Authentication authentication) {
        PasswordAuthentication passwordAuthentication = passwordAuthentication(authentication);
        return (U) passwordAuthentication.getUser((LongRepository<User>) userRepository);
    }

    public U get(String username) {
        List<U> users = userRepository.find(User.Fields.Username, username, LIMIT_ONE);
        return users.size() > 0 ? users.get(0) : null;
    }

    public void delete(U entity) {
        userRepository.delete(entity);
    }

    public List<U> find(String key, Object value, int limit) {
        return userRepository.find(key, value, limit);
    }

    public List<U> find(Map<String, Object> properties, int limit) {
        return userRepository.find(properties, limit);
    }

    private PasswordAuthentication passwordAuthentication(Authentication authentication) {
        PasswordAuthentication passwordAuthentication = Cast.as(authentication, PasswordAuthentication.class);
        if (passwordAuthentication == null) {
            throw new UserServiceException("Unable to work with authentication %s, it must be a %s to be stored/found in the database", authentication, PasswordAuthentication.class.getSimpleName());
        }
        return passwordAuthentication;
    }
}
