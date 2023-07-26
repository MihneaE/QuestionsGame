package com.example.mpp_examn;

public class Question {
    private Integer id;
    private String question;
    private String correctAnswer;
    private int difficulty;

    public Question() {}

    public Question(Integer id,String question, String correctAnswer, int difficulty)
    {
        this.id = id;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    @Override
    public String toString() {
        return "Question: " + question + " Answer: " + correctAnswer + " Difficulty: " + difficulty;
    }
}
