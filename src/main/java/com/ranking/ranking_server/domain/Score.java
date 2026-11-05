package com.ranking.ranking_server.domain;

import java.time.LocalDateTime;

public class Score {
    private final String userId;
    private long score;
    private LocalDateTime achievedAt;

    public Score(String userId, long score, LocalDateTime achievedAt) {
        this.userId = userId;
        this.score = score;
        this.achievedAt = achievedAt;
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
