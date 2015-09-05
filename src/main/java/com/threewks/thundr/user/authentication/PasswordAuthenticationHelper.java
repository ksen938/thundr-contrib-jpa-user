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
package com.threewks.thundr.user.authentication;

/**
 * Password hashing and salting functions. Delegating here is a bit weird but necessitated by the fact
 * that we don't want to reimplement these functions (and potentially miss critical security patches) however the
 * generic design of {@link BasePasswordAuthentication} doesn't enable its use as a JPA entity (requires @Entity
 * annotation as of JPA 2.1)
 */
public class PasswordAuthenticationHelper {

    private static final BasePasswordAuthentication delegate = new BasePasswordAuthentication() {};

    private PasswordAuthenticationHelper() {}

    public static String hash(String password, byte[] salt, int iterations, String digestAlgorithm) {
        return delegate.hash(password, salt, iterations, digestAlgorithm);
    }

    public static byte[] salt(int bytes) {
        return delegate.salt(bytes);
    }
}
