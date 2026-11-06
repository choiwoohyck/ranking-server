package com.ranking.ranking_server.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {
    @Id
    private String userId;
    private String passwordHash;

    public User(String userId, String passwordHash) {
        this.userId = userId;
        this.passwordHash = passwordHash;
    }

    protected User() {

    }

    public String getUserId() {
        return userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
