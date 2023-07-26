package com.example.mpp_examn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import java.sql.*;


public class GameApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Question question = new Question(1, "x", "y", 1);
        Player player = new Player(0, "", 0, 0, 0);

        List<Question> questions = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        SQLOpperations sqlOpperations = new SQLOpperations(players, questions);
        RankingSort rankingSort = new RankingSort();
        Game game = new Game(player, questions, players, sqlOpperations, rankingSort);
        sqlOpperations.generateAndLoadSQL();

        Label aliasLabel = new Label("Introduceti aliasul:");
        Label IdLabel = new Label("Introduceti ID:");
        TextField aliasField = new TextField();
        TextField idField = new TextField();

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setHgap(10);
        layout.setVgap(10);
        layout.add(IdLabel, 0,0);
        layout.add(idField, 1, 0);
        layout.add(aliasLabel, 0, 1);
        layout.add(aliasField, 1,1);

        Button startButton = new Button("START GAME");

        VBox finalBox = new VBox(20, layout, startButton);
        finalBox.setAlignment(Pos.CENTER);

        final Label questionLabel = new Label();
        Label answerLabel = new Label("YOUR ANSWER: ");
        TextField answerField = new TextField();
        Button submitButton = new Button("Submit");
        Button continueButton = new Button("Continue");

        startButton.setOnAction(actionEvent -> {
            Integer id = Integer.parseInt(idField.getText());
            String alias = aliasField.getText();

            Player player2 = new Player(id, alias, 0, 0, 0);

            if (!game.startGame(id, alias, player2))
            {
                Stage newWINDOW = new Stage();

                Label label1 = new Label("ALIAS SI ID INREGISTRAT");
                Label label2 = new Label("INTRODUCETI DIN NOU ID-UL:");
                Label label3 = new Label("INTRODUCETI DUN NOU ALIASUL:");
                TextField id2 = new TextField();
                TextField alias2 = new TextField();
                Button startButton2 = new Button("START");

                GridPane gridPane = new GridPane();
                gridPane.setPadding(new Insets(10, 10, 10, 10));
                gridPane.setHgap(10);
                gridPane.setVgap(10);

                gridPane.add(label2, 0, 0);
                gridPane.add(id2, 1, 0);
                gridPane.add(label3, 0, 1);
                gridPane.add(alias2,1, 1);

                VBox vBox = new VBox(20, label1, gridPane, startButton2);
                vBox.setAlignment(Pos.CENTER);

                startButton2.setOnAction(actionEvent1 -> {
                    Integer newID = Integer.parseInt(id2.getText());
                    String newAlias = alias2.getText();

                    Player player1 = new Player(newID, newAlias, 0, 0, 0);

                    if (game.startGame(newID, newAlias, player1))
                    {
                        newWINDOW.close();
                        game.answerFirstQuestion(questionLabel, answerLabel, answerField, submitButton, continueButton);
                    }
                    else
                    {
                        newWINDOW.close();

                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Wrong user!", ButtonType.OK);
                        alert.setHeaderText(null);
                        alert.setTitle("INFO");

                        alert.showAndWait();
                    }
                });

                Scene secondScene = new Scene(vBox, 320, 240);

                newWINDOW.setScene(secondScene);
                newWINDOW.initModality(Modality.APPLICATION_MODAL);
                newWINDOW.setTitle("REGISTRATION WINDOW");
                newWINDOW.showAndWait();
            }
            else
            {
                game.answerFirstQuestion(questionLabel, answerLabel, answerField, submitButton, continueButton);
            }
        });

        Scene scene = new Scene(finalBox, 400, 200);
        stage.setTitle("QUESTION GAME!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}