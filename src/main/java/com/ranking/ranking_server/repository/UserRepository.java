package com.ranking.ranking_server.repository;

import com.ranking.ranking_server.domain.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
}
