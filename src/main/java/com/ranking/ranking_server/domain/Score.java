package com.ranking.ranking_server.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Score {
    @Id
    private String userId;
    private long score;
    private LocalDateTime achievedAt;

    public Score(String userId, long score, LocalDateTime achievedAt) {
        this.userId = userId;
        this.score = score;
        this.achievedAt = achievedAt;
    }

    protected Score() {

    }

    public String getUserId() {
        return userId;
    }

    public long getScore() {
        return score;
    }

    public LocalDateTime getAchievedAt(){
        return achievedAt;
    }

    public boolean updateIfHigher(long newScore, LocalDateTime achievedAt){
        boolean result = newScore > score;

        if (result) {
            score = newScore;
            this.achievedAt = achievedAt;
        }

        return result;
    }

}
