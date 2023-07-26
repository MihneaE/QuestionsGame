package com.example.mpp_examn;

import java.util.List;
import java.sql.*;
import java.util.Random;
import java.util.concurrent.Callable;

public class SQLOpperations {
    private List<Player> players;
    private List<Question> questions;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public SQLOpperations(List<Player> players, List<Question> questions)
    {
        this.players = players;
        this.questions = questions;
        this.connection = null;
        this.statement = null;
        this.resultSet = null;

        try {

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:questions.db");
        }
        catch (Exception e)
        {
            System.err.println(e.getClass().getName() + " " + e.getMessage());
            System.exit(0);
        }
    }

    private Connection connect()
    {
        String url = "jdbc:sqlite:questions.db";
        Connection conn = null;

        try
        {
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return conn;
    }

    public void generateAndLoadSQL()
    {
        try
        {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Questions");

            while (resultSet.next())
            {
                Integer id =resultSet.getInt("ID");
                String questionT = resultSet.getString("Question");
                String answer = resultSet.getString("Answer");
                Integer difficulty = resultSet.getInt("Difficulty");

                Question question = new Question(id, questionT, answer, difficulty);
                this.questions.add(question);
            }

            String countQuery = "SELECT COUNT(*) FROM Users";
            ResultSet countResult = statement.executeQuery(countQuery);
            countResult.next();
            int rowCount = countResult.getInt(1);

            if (rowCount == 0)
            {
                Random random = new Random();
                String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
                int length = 5;

                for (int i = 0; i < 10; ++i)
                {
                    int id = i + 1;

                    StringBuilder result = new StringBuilder(length);

                    for (int j = 0; j < length; ++j)
                        result.append(alphabet.charAt(random.nextInt(alphabet.length())));

                    String sql = "INSERT INTO Users (ID, Alias, Score, NrCorrectQuestions, Ranking) VALUES (" + id + ", '" + result + "', '" + 0 + "', '" + 0 + "', '" + 0 + "')";
                    statement.executeUpdate(sql);
                }
            }

            resultSet = statement.executeQuery("SELECT * FROM Users");

            while (resultSet.next())
            {
                Integer id = resultSet.getInt("ID");
                String alias = resultSet.getString("Alias");
                Integer score = resultSet.getInt("Score");
                Integer numberQ = resultSet.getInt("NrCorrectQuestions");
                Integer ranking = resultSet.getInt("Ranking");

                Player player = new Player(id, alias, score, numberQ, ranking);
                players.add(player);
            }

        }
        catch (Exception e)
        {
            System.err.println(e.getClass().getName() + " " + e.getMessage());
            System.exit(0);
        }
        finally
        {
            try
            {
                if (connection != null)
                    connection.close();
                if (statement != null)
                    statement.close();
                if (resultSet != null)
                    resultSet.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addPlayerToDataBase(Player player)
    {
        String sql = "INSERT INTO Users(ID, Alias, Score, NrCorrectQuestions, Ranking) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, player.getId());
            pstmt.setString(2, player.getAlias());
            pstmt.setInt(3, player.getScore());
            pstmt.setInt(4, player.getCorrectAnswers());
            pstmt.setInt(5, player.getRanking());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerFromDataBase(int id, String alias)
    {
        String sql = "DELETE FROM Users WHERE ID = ? AND Alias = ?";

        try(Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, id);
            pstmt.setString(2, alias);

            pstmt.executeUpdate();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void updatePlayerFromDataBase(Player player)
    {
        String sql = "UPDATE Users SET Score = ? , NoCorrectQuestions = ?, Ranking = ? WHERE ID = ? and Alias = ?";

        try(PreparedStatement pstmt = this.connection.prepareStatement(sql))
        {
            pstmt.setInt(1, player.getScore());
            pstmt.setInt(2, player.getCorrectAnswers());
            pstmt.setInt(3, player.getRanking());
            pstmt.setInt(4, player.getId());
            pstmt.setString(5, player.getAlias());

            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
