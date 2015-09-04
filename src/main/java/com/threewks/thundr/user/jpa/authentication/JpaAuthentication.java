package com.threewks.thundr.user.jpa.authentication;

import com.threewks.thundr.jpa.repository.LongRepository;
import com.threewks.thundr.user.authentication.Authentication;
import com.threewks.thundr.user.jpa.User;

/**
 * Authentications stored into the User object with JPA implement this interface.
 */
public interface JpaAuthentication<Self extends JpaAuthentication<Self>> extends Authentication {
    public void setUser(User user);

    /**
     * Find the user for this type of authentication. If this authentication is unverified, a search
     * may need to be performed, if it is verified the user will already have been set previously.
     *
     * @param userRepository
     * @return the user for this authentication, or null if none
     */
    public User getUser(LongRepository<User> userRepository);

    /**
     * Find the authentication matching the given one - that is the authentication that is of the same type for the same user.
     *
     * @param passwordAuthenticationRepository
     * @param authentication
     * @return
     */
    public Self getMatchingAuthentication(LongRepository<PasswordAuthentication> passwordAuthenticationRepository, Self authentication);
}
