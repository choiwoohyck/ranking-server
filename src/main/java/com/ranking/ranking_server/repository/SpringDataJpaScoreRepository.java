package com.ranking.ranking_server.repository;

import com.ranking.ranking_server.domain.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaScoreRepository extends JpaRepository<Score,String>, ScoreRepository {

}
