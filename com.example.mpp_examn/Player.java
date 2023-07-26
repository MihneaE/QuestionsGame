package com.example.mpp_examn;

public class Player {
    private Integer id;
    private String alias;
    private int score;
    private int correctAnswers;
    private int ranking;

    public Player() {}

    public Player(Integer id, String alias, int score, int correctAnswers, int ranking)
    {
        this.id = id;
        this.alias = alias;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.ranking = ranking;
    }

    public Player(int score, int correctAnswers, int ranking)
    {
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.ranking = ranking;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAlias() {
        return alias;
    }

    public int getScore() {
        return score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getRanking() {
        return ranking;
    }

    public void addToScore(int difficulty)
    {
        this.score += Math.pow(10, difficulty - 1);
    }

    public void incrementCorrectQuestions()
    {
        this.correctAnswers++;
    }

    @Override
    public String toString() {
        return "ID: " + id + " Alias: " + alias + " Score: " + score + " Correct Answers: " + correctAnswers + " Ranking: " + ranking;
    }
}
