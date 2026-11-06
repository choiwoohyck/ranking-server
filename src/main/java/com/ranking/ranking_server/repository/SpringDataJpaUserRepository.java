package com.ranking.ranking_server.repository;

import com.ranking.ranking_server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaUserRepository extends JpaRepository<User,String>,UserRepository {
}
