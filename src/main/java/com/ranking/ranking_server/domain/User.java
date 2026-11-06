package com.ranking.ranking_server.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {
    @Id
    private String userId;
    private String password;

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    protected User() {

    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }
}
