package com.threewks.thundr.user.jpa;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.threewks.thundr.user.jpa.converter.DateConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="user")
public class User implements com.threewks.thundr.user.User {

    public static class Fields {
        public final static String Id = "id";
        public final static String Username = "username";
        public final static String Email = "email";
        public final static String Created = "created";
        public final static String LastLogin = "lastLogin";

        private Fields() {}
    }

    /**
     * Default constructor for JPA/Hibernate only
     */
    public User() {
    }

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="email")
    private String email;

    @Column(name="username")
    private String username;

    @Column(name="created")
    @Convert(converter = DateConverter.class)
    private DateTime created;

    @Column(name="last_login")
    @Convert(converter = DateConverter.class)
    private DateTime lastLogin;

    @ElementCollection
    @CollectionTable(name = "user_properties", joinColumns = @JoinColumn(name="user_id"))
    @MapKeyColumn(name="name")
    @Column(name = "value")
    private Map<String, String> properties = Maps.newHashMap();

    @ElementCollection
    @CollectionTable(name="user_role", joinColumns = @JoinColumn(name="user_id"))
    @Column(name="role_name")
    private Set<String> roles = Sets.newHashSet();

    public User(String username, String email) {
        this.created = DateTime.now(DateTimeZone.UTC);
        this.username = username;
        this.email = email;
    }


    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public String getProperty(String property) {
        return properties.get(property);
    }

    @Override
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public void removeProperty(String key) {
        properties.remove(key);
    }

    @Override
    public DateTime getCreated() {
        return created;
    }

    @Override
    public DateTime getLastLogin() {
        return lastLogin;
    }

    @Override
    public void setLastLogin(DateTime dateTime) {
        lastLogin = dateTime;
    }

    @Override
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public void setRoles(Collection<String> roles) {
        this.roles = Sets.newHashSet(roles);
    }

    @Override
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    @Override
    public boolean hasRoles(String... roles) {
        return hasRoles(Arrays.asList(roles));
    }

    @Override
    public boolean hasRoles(Collection<String> roles) {
        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addRole(String role) {
        roles.add(role);
    }

    @Override
    public void removeRole(String role) {
        roles.remove(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return new EqualsBuilder()
                .append(username, user.username)
                .append(email, user.email)
                .append(created, user.created)
                .append(lastLogin, user.lastLogin)
                .append(properties, user.properties)
                .append(roles, user.roles)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(username)
                .append(email)
                .append(created)
                .append(lastLogin)
                .append(properties)
                .append(roles)
                .toHashCode();
    }
}
