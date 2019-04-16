package main;

import main.Human;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String url = "jdbc:postgresql://localhost:5432/forum";
    private static final String SQL_CREATE_HUMAN_TABLE = "CREATE TABLE IF NOT EXISTS human(id INT PRIMARY KEY, name VARCHAR(20), age INT);";
    private static final String SQL_INSERT_HUMAN = "INSERT INTO human (id, name, age) VALUES (?, ?, ?)";
    private static final String SQL_READ_HUMAN = "SELECT * FROM human";
    private static final String user = "user1";
    private static final String password = "123456";

    public static void main(String[] args)  {
        List<Human> humanList = new ArrayList<>();
        humanList.add(new Human(1,"Viktor", 21));
        humanList.add(new Human(2,"Misha", 21));
        humanList.add(new Human(3,"Max", 21));

        try (Connection db = DriverManager.getConnection(url, user, password);
        Statement statement = db.createStatement()){
            statement.execute(SQL_CREATE_HUMAN_TABLE);
            readHumansToDB(db).forEach(System.out::println);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static List<Human> readHumansToDB(Connection db) {
        List<Human> humanList = new ArrayList<>();

        try (Statement statement = db.createStatement();
        ResultSet resultSet = statement.executeQuery(SQL_READ_HUMAN)){
            while (resultSet.next()) {
                Human human = new Human();
                human.setId(resultSet.getInt("id"));
                human.setName(resultSet.getString("name"));
                human.setAge(resultSet.getInt("age"));
                humanList.add(human);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return humanList;
    }

    public static void addHumansToDB(Connection db, List<Human> humanList) {
        try (PreparedStatement preparedStatement = db.prepareStatement(SQL_INSERT_HUMAN, Statement.RETURN_GENERATED_KEYS)){
            for (Human human: humanList) {
                preparedStatement.setInt(1, human.getId());
                preparedStatement.setString(2, human.getName());
                preparedStatement.setInt(3, human.getAge());
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    human.setId(resultSet.getInt(1));
                }
                resultSet.close();
            }
            humanList.forEach(System.out::println);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
