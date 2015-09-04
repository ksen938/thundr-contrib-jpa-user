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

    @ManyToOne(fetch = FetchType.LAZY)
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
