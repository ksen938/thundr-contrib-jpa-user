package com.threewks.thundr.user.authentication;

import com.threewks.thundr.user.authentication.BasePasswordAuthentication;

/**
 * Password hashing and salting functions. This fact we're delegating here is a bit weird but necessitated by the fact
 * that we don't want to reimplement these functions (and potentially miss critical security patches) however the
 * design of {@link BasePasswordAuthentication} precludes its use as a JPA entity.
 */
public class PasswordAuthenticationHelper {

    private static final BasePasswordAuthentication delegate = new BasePasswordAuthentication() {};

    public static String hash(String password, byte[] salt, int iterations, String digestAlgorithm) {
        return delegate.hash(password, salt, iterations, digestAlgorithm);
    }

    public static byte[] salt(int bytes) {
        return delegate.salt(bytes);
    }

    public static boolean validates(String authorisation) {
        return delegate.validates(authorisation);
    }
}
