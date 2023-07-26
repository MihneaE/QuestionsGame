package com.example.mpp_examn;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Player player;
    private List<Question> questions;
    private boolean isWin;
    private boolean isRegistered;
    private boolean isCorrectAnswer;
    private List<Player> players;
    private SQLOpperations sqlOpperations;
    private RankingSort rankingSort;

    public Game() {}

    public Game(Player player, List<Question> questions, List<Player> players, SQLOpperations sqlOpperations, RankingSort rankingSort)
    {
        this.player = player;
        this.questions = questions;
        this.players = players;
        this.sqlOpperations  = sqlOpperations;
        this.isWin = false;
        this.rankingSort = rankingSort;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setIsWin(boolean isWin) {
        this.isWin = isWin;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        isCorrectAnswer = correctAnswer;
    }

    public boolean isCorrectAnswer() {
        return isCorrectAnswer;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Player getPlayer() {
        return player;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public boolean Registered(Integer id, String alias, Player player)
    {
        boolean contains = false;

        for (int i = 0; i  < players.size(); ++i)
            if (players.get(i).getId().equals(id) && players.get(i).getAlias().equals(alias))
                contains = true;

        if (contains)
            this.setRegistered(true);
        else
        {
            players.add(player);
            sqlOpperations.addPlayerToDataBase(player);
            this.setRegistered(false);
        }

        player.setId(id);
        player.setAlias(alias);

        return this.isRegistered();
    }

    public boolean startGame(Integer id, String alias, Player player)
    {
        if (Registered(id, alias, player))
            return true;
        return false;
    }

    public void updateQuestionLabel(Label questionLabel, Question newQuestion)
    {
        questionLabel.setText(newQuestion.getQuestion());
    }

    public void answerFirstQuestion(Label questionLabel, Label answerLabel, TextField answerField, Button submitButton, Button continueButton)
    {
        List<Question> lvl1Questions = new ArrayList<>();

        for (int i = 0; i < questions.size(); ++i)
            if (questions.get(i).getDifficulty() == 1)
                lvl1Questions.add(questions.get(i));

        Random random = new Random();

        int randomIndex = random.nextInt(lvl1Questions.size());
        Question selectedQuestion = lvl1Questions.get(randomIndex);

        this.questions.remove(selectedQuestion);

        Stage questionWindow = new Stage();
        questionWindow.initModality(Modality.APPLICATION_MODAL);
        questionWindow.setTitle("FIRST QUESTION");

        this.updateQuestionLabel(questionLabel, selectedQuestion);
        continueButton.setVisible(false);

        submitButton.setOnAction(actionEvent -> {
            String answer = answerField.getText();

            if (answer.equals(selectedQuestion.getCorrectAnswer()))
            {
                answerLabel.setText("Correct answer!");
                submitButton.setVisible(false);
                answerField.setEditable(false);
                continueButton.setVisible(true);

                this.player.addToScore(selectedQuestion.getDifficulty());
                this.player.incrementCorrectQuestions();

                this.setCorrectAnswer(true);
            }
            else
            {
                answerLabel.setText("Wrong answer!");
                answerField.setEditable(false);
                submitButton.setVisible(false);
                continueButton.setVisible(true);

                this.setCorrectAnswer(false);
            }
        });

        continueButton.setOnAction(actionEvent2 -> {

                questionWindow.close();

                if (this.isCorrectAnswer())
                {
                    Platform.runLater(() -> {
                        answerSecondQuestion(questionLabel, answerLabel, answerField, submitButton, continueButton);
                    });
                }
                else
                {
                    answerFirstQuestionAgain(questionLabel, answerLabel, answerField, submitButton, continueButton);
                }
        });

        HBox answerBox = new HBox(20, answerLabel, answerField);
        HBox buttonBox = new HBox(20, submitButton, continueButton);

        VBox finalBox = new VBox(20, questionLabel, answerBox, buttonBox);
        finalBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(finalBox);
        questionWindow.setScene(scene);
        questionWindow.showAndWait();
    }

    public void answerFirstQuestionAgain(Label questionLabel, Label answerLabel, TextField answerField, Button submitButton, Button continueButton)
    {
        List<Question> lvl1Questions = new ArrayList<>();

        for (int i = 0; i < questions.size(); ++i)
            if (questions.get(i).getDifficulty() == 1)
                lvl1Questions.add(questions.get(i));

        Random random = new Random();

        int randomIndex = random.nextInt(lvl1Questions.size());
        Question selectedQuestion = lvl1Questions.get(randomIndex);

        Stage questionWindow = new Stage();
        questionWindow.initModality(Modality.APPLICATION_MODAL);
        questionWindow.setTitle("FIRST QUESTION AGAIN");

        this.updateQuestionLabel(questionLabel, selectedQuestion);
        continueButton.setVisible(false);
        answerField.setEditable(true);
        answerLabel.setText("YOUR ANSWER: ");
        submitButton.setVisible(true);

        submitButton.setOnAction(actionEvent -> {
            String answer = answerField.getText();

            if (answer.equals(selectedQuestion.getCorrectAnswer()))
            {
                answerLabel.setText("Correct answer!");
                submitButton.setVisible(false);
                answerField.setEditable(false);
                continueButton.setVisible(true);

                this.player.addToScore(selectedQuestion.getDifficulty());
                this.player.incrementCorrectQuestions();

                this.setCorrectAnswer(true);
            }
            else
            {
                answerLabel.setText("Wrong answer!");
                answerField.setEditable(false);
                submitButton.setVisible(false);
                continueButton.setVisible(true);

                this.setCorrectAnswer(false);
            }
        });

        continueButton.setOnAction(actionEvent2 -> {
            questionWindow.close();

            if (this.isCorrectAnswer())
            {
                Platform.runLater(() -> {
                    answerSecondQuestion(questionLabel, answerLabel, answerField, submitButton, continueButton);
                });
            }
            else
            {
                this.setIsWin(false);
                this.gameOver();
            }
        });

            HBox answerBox = new HBox(20, answerLabel, answerField);
            HBox buttonBox = new HBox(20, submitButton, continueButton);

            VBox finalBox = new VBox(20, questionLabel, answerBox, buttonBox);
            finalBox.setAlignment(Pos.CENTER);

            Scene scene = new Scene(finalBox);
            questionWindow.setScene(scene);
            questionWindow.showAndWait();

    }

    public void answerSecondQuestion(Label questionLabel, Label answerLabel, TextField answerField, Button submitButton, Button continueButton)
    {
        List<Question> lvl2Questions = new ArrayList<>();

        for (int i = 0; i < questions.size(); ++i)
            if (questions.get(i).getDifficulty() == 2)
                lvl2Questions.add(questions.get(i));

        Random random = new Random();

        int randomIndex = random.nextInt(lvl2Questions.size());
        Question selectedQuestion = lvl2Questions.get(randomIndex);

        Stage questionWindow = new Stage();
        questionWindow.initModality(Modality.APPLICATION_MODAL);
        questionWindow.setTitle("SECOND QUESTION");

        this.updateQuestionLabel(questionLabel, selectedQuestion);
        continueButton.setVisible(false);
        answerField.setEditable(true);
        answerLabel.setText("YOUR ANSWER: ");
        submitButton.setVisible(true);

        this.questions.remove(selectedQuestion);

        submitButton.setOnAction(actionEvent -> {
            String answer = answerField.getText();

            if (answer.equals(selectedQuestion.getCorrectAnswer()))
            {
                System.out.println("Correct answer!\n");
                answerLabel.setText("Correct answer!");
                submitButton.setVisible(false);
                answerField.setEditable(false);
                continueButton.setVisible(true);

                this.player.addToScore(selectedQuestion.getDifficulty());
                this.player.incrementCorrectQuestions();

                this.setCorrectAnswer(true);
            }
            else
            {
                answerLabel.setText("Wrong answer!");
                answerField.setEditable(false);
                submitButton.setVisible(false);
                continueButton.setVisible(true);

                this.setCorrectAnswer(false);
            }
        });

        continueButton.setOnAction(actionEvent -> {
            questionWindow.close();

            if (this.isCorrectAnswer())
            {
                Platform.runLater(() -> {
                    answerThirdQuestion(questionLabel, answerLabel, answerField, submitButton, continueButton);
                });
            }
            else
            {
                answerSecondQuestionAgain(questionLabel, answerLabel, answerField, submitButton, continueButton);
            }
        });

        HBox answerBox = new HBox(20, answerLabel, answerField);
        HBox buttonBox = new HBox(20, submitButton, continueButton);

        VBox finalBox = new VBox(20, questionLabel, answerBox, buttonBox);
        finalBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(finalBox);
        questionWindow.setScene(scene);
        questionWindow.showAndWait();
    }

    public void answerSecondQuestionAgain(Label questionLabel, Label answerLabel, TextField answerField, Button submitButton, Button continueButton)
    {
        List<Question> lvl2Questions = new ArrayList<>();

        for (int i = 0; i < questions.size(); ++i)
            if (questions.get(i).getDifficulty() == 2)
                lvl2Questions.add(questions.get(i));

        Random random = new Random();

        int randomIndex = random.nextInt(lvl2Questions.size());
        Question selectedQuestion = lvl2Questions.get(randomIndex);

        Stage questionWindow = new Stage();
        questionWindow.initModality(Modality.APPLICATION_MODAL);
        questionWindow.setTitle("SECOND QUESTION AGAIN");

        this.updateQuestionLabel(questionLabel, selectedQuestion);
        continueButton.setVisible(false);
        answerField.setEditable(true);
        answerField.setText("YOUR ANSWER: ");
        submitButton.setVisible(true);

        submitButton.setOnAction(actionEvent -> {
            String answer = answerField.getText();

            if (answer.equals(selectedQuestion.getCorrectAnswer()))
            {
                answerLabel.setText("Correct answer!");
                submitButton.setVisible(false);
                answerField.setEditable(false);
                continueButton.setVisible(true);

                this.player.addToScore(selectedQuestion.getDifficulty());
                this.player.incrementCorrectQuestions();
            }
            else
            {
                answerLabel.setText("Wrong answer!");
                answerField.setEditable(false);
                submitButton.setVisible(false);
                continueButton.setVisible(true);

                this.setCorrectAnswer(false);
            }
        });


            continueButton.setOnAction(actionEvent2 -> {

                questionWindow.close();

                if (this.isCorrectAnswer())
                {
                    answerThirdQuestion(questionLabel, answerLabel, answerField, submitButton, continueButton);
                }
                else
                {
                    this.setIsWin(false);
                    this.gameOver();
                }
            });

            HBox answerBox = new HBox(20, answerLabel, answerField);
            HBox buttonBox = new HBox(20, submitButton, continueButton);

            VBox finalBox = new VBox(20, questionLabel, answerBox, buttonBox);
            finalBox.setAlignment(Pos.CENTER);

            Scene scene = new Scene(finalBox);
            questionWindow.setScene(scene);
            questionWindow.showAndWait();

    }

    public void answerThirdQuestion(Label questionLabel, Label answerLabel, TextField answerField, Button submitButton, Button continueButton)
    {
        List<Question> lvl3Questions = new ArrayList<>();

        for (int i = 0; i < questions.size(); ++i)
            if (questions.get(i).getDifficulty() == 3)
                lvl3Questions.add(questions.get(i));

        Random random = new Random();

        int randomIndex = random.nextInt(lvl3Questions.size());
        Question selectedQuestion = lvl3Questions.get(randomIndex);

        Stage questionWindow = new Stage();
        questionWindow.initModality(Modality.APPLICATION_MODAL);
        questionWindow.setTitle("THIRD QUESTION");

        this.updateQuestionLabel(questionLabel, selectedQuestion);
        continueButton.setVisible(false);
        answerField.setEditable(true);
        answerLabel.setText("YOUR ANSWER: ");
        submitButton.setVisible(true);

        this.questions.remove(selectedQuestion);

        submitButton.setOnAction(actionEvent -> {
            String answer = answerField.getText();

            if (answer.equals(selectedQuestion.getCorrectAnswer()))
            {
                System.out.println("Correct answer!\n");
                answerLabel.setText("Correct answer!");
                submitButton.setVisible(false);
                answerField.setEditable(false);
                continueButton.setVisible(true);

                this.player.addToScore(selectedQuestion.getDifficulty());
                this.player.incrementCorrectQuestions();

                this.setCorrectAnswer(true);
            }
            else
            {
                answerLabel.setText("Wrong answer!");
                answerField.setEditable(false);
                submitButton.setVisible(false);
                continueButton.setVisible(true);

                this.setCorrectAnswer(false);
            }
        });

        continueButton.setOnAction(actionEvent -> {
            questionWindow.close();

            if (this.isCorrectAnswer())
            {
                answerFourQuestion(questionLabel, answerLabel, answerField, submitButton, continueButton);
            }
            else
                answerThirdQuestionAgain(questionLabel, answerLabel, answerField, submitButton, continueButton);
        });

        HBox answerBox = new HBox(20, answerLabel, answerField);
        HBox buttonBox = new HBox(20, submitButton, continueButton);

        VBox finalBox = new VBox(20, questionLabel, answerBox, buttonBox);
        finalBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(finalBox);
        questionWindow.setScene(scene);
        questionWindow.showAndWait();
    }

    public void answerThirdQuestionAgain(Label questionLabel, Label answerLabel, TextField answerField, Button submitButton, Button continueButton)
    {
        List<Question> lvl3Questions = new ArrayList<>();

        for (int i = 0; i < questions.size(); ++i)
            if (questions.get(i).getDifficulty() == 3)
                lvl3Questions.add(questions.get(i));

        Random random = new Random();

        int randomIndex = random.nextInt(lvl3Questions.size());
        Question selectedQuestion = lvl3Questions.get(randomIndex);

        Stage questionWindow = new Stage();
        questionWindow.initModality(Modality.APPLICATION_MODAL);
        questionWindow.setTitle("THIRD QUESTION AGAIN");

        this.updateQuestionLabel(questionLabel, selectedQuestion);
        continueButton.setVisible(false);
        answerField.setEditable(true);
        answerField.setText("YOUR ANSWER: ");
        submitButton.setVisible(true);

        submitButton.setOnAction(actionEvent -> {
            String answer = answerField.getText();

            if (answer.equals(selectedQuestion.getCorrectAnswer()))
            {
                answerLabel.setText("Correct answer!");
                submitButton.setVisible(false);
                answerField.setEditable(false);
                continueButton.setVisible(true);

                this.player.addToScore(selectedQuestion.getDifficulty());
                this.player.incrementCorrectQuestions();
            }
            else
            {
                answerLabel.setText("Wrong answer!");
                answerField.setEditable(false);
                submitButton.setVisible(false);
                continueButton.setVisible(true);

                this.setCorrectAnswer(false);
            }
        });

            continueButton.setOnAction(actionEvent2 -> {

                questionWindow.close();

                if (this.isCorrectAnswer())
                {
                    this.answerFourQuestion(questionLabel, answerLabel, answerField, submitButton, continueButton);
                }
                else
                {
                    this.setIsWin(false);
                    this.gameOver();
                }

            });

            HBox answerBox = new HBox(20, answerLabel, answerField);
            HBox buttonBox = new HBox(20, submitButton, continueButton);

            VBox finalBox = new VBox(20, questionLabel, answerBox, buttonBox);
            finalBox.setAlignment(Pos.CENTER);

            Scene scene = new Scene(finalBox);
            questionWindow.setScene(scene);
            questionWindow.showAndWait();

    }

    public void answerFourQuestion(Label questionLabel, Label answerLabel, TextField answerField, Button submitButton, Button continueButton)
    {
        List<Question> lvl4Questions = new ArrayList<>();

        for (int i = 0; i < questions.size(); ++i)
            if (questions.get(i).getDifficulty() == 4)
                lvl4Questions.add(questions.get(i));

        Random random = new Random();

        int randomIndex = random.nextInt(lvl4Questions.size());
        Question selectedQuestion = lvl4Questions.get(randomIndex);

        Stage questionWindow = new Stage();
        questionWindow.initModality(Modality.APPLICATION_MODAL);
        questionWindow.setTitle("THIRD QUESTION");

        this.updateQuestionLabel(questionLabel, selectedQuestion);
        continueButton.setVisible(false);
        answerField.setEditable(true);
        answerLabel.setText("YOUR ANSWER: ");
        submitButton.setVisible(true);

        this.questions.remove(selectedQuestion);

        submitButton.setOnAction(actionEvent -> {
            String answer = answerField.getText();

            if (answer.equals(selectedQuestion.getCorrectAnswer()))
            {
                answerLabel.setText("JOCUL S-A TERMINAT! AI CASTIGAT!");
                submitButton.setVisible(false);
                answerField.setEditable(false);
                continueButton.setVisible(true);

                this.player.addToScore(selectedQuestion.getDifficulty());
                this.player.incrementCorrectQuestions();

                this.setCorrectAnswer(true);
            }
            else
            {
                answerLabel.setText("Wrong answer!");
                answerField.setEditable(false);
                submitButton.setVisible(false);
                continueButton.setVisible(true);

                this.setCorrectAnswer(false);
            }
        });

        continueButton.setOnAction(actionEvent -> {
            questionWindow.close();

            if (this.isCorrectAnswer())
            {
                this.setIsWin(true);
                this.gameOver();
            }
            else
                answerFourQuestionAngain(questionLabel, answerLabel, answerField, submitButton, continueButton);
        });

        HBox answerBox = new HBox(20, answerLabel, answerField);
        HBox buttonBox = new HBox(20, submitButton, continueButton);

        VBox finalBox = new VBox(20, questionLabel, answerBox, buttonBox);
        finalBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(finalBox);
        questionWindow.setScene(scene);
        questionWindow.showAndWait();
    }

    public void answerFourQuestionAngain(Label questionLabel, Label answerLabel, TextField answerField, Button submitButton, Button continueButton)
    {
        List<Question> lvl4Questions = new ArrayList<>();

        for (int i = 0; i < questions.size(); ++i)
            if (questions.get(i).getDifficulty() == 4)
                lvl4Questions.add(questions.get(i));

        Random random = new Random();

        int randomIndex = random.nextInt(lvl4Questions.size());
        Question selectedQuestion = lvl4Questions.get(randomIndex);

        Stage questionWindow = new Stage();
        questionWindow.initModality(Modality.APPLICATION_MODAL);
        questionWindow.setTitle("FOUR QUESTION AGAIN");

        this.updateQuestionLabel(questionLabel, selectedQuestion);
        continueButton.setVisible(false);
        answerField.setEditable(true);
        answerField.setText("YOUR ANSWER: ");
        submitButton.setVisible(true);

        submitButton.setOnAction(actionEvent -> {
            String answer = answerField.getText();

            if (answer.equals(selectedQuestion.getCorrectAnswer()))
            {
                answerLabel.setText("Correct answer!");
                submitButton.setVisible(false);
                answerField.setEditable(false);
                continueButton.setVisible(true);

                this.player.addToScore(selectedQuestion.getDifficulty());
                this.player.incrementCorrectQuestions();
            }
            else
            {
                answerLabel.setText("Wrong answer!");
                answerField.setEditable(false);
                submitButton.setVisible(false);
                continueButton.setVisible(true);

                this.setCorrectAnswer(false);
            }
        });


            continueButton.setOnAction(actionEvent2 -> {
                questionWindow.close();

                if (this.isCorrectAnswer())
                {
                    this.setIsWin(true);
                    this.gameOver();
                }
                else
                {
                    this.setIsWin(false);
                    this.gameOver();
                }
            });

            HBox answerBox = new HBox(20, answerLabel, answerField);
            HBox buttonBox = new HBox(20, submitButton, continueButton);

            VBox finalBox = new VBox(20, questionLabel, answerBox, buttonBox);
            finalBox.setAlignment(Pos.CENTER);

            Scene scene = new Scene(finalBox);
            questionWindow.setScene(scene);
            questionWindow.showAndWait();

    }

    public void inMemoryUpdate()
    {
        for (int i = 0; i < players.size(); ++i)
        {
            if (players.get(i).getId().equals(player.getId()) && players.get(i).getAlias().equals(player.getAlias()))
            {
                players.get(i).setScore(player.getScore());
                players.get(i).setCorrectAnswers(player.getCorrectAnswers());
            }
        }
    }

    public void gameOver()
    {
        if (isWin())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game is over! You win!", ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("GAME OVER!");

            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game is over! You lost!", ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("GAME OVER!");

            alert.showAndWait();
        }

        rankingSort.setRankingPlayers(this.players);
        this.inMemoryUpdate();
        sqlOpperations.updatePlayerFromDataBase(this.player);
    }

}
