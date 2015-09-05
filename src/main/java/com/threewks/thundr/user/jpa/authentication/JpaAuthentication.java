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
