package ru.gb.server.database;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseHandler {
    private static Connection dbConnection;
    private static Statement statement;
    private boolean haveBase = false;

    public static void main(String[] args) {
//        DatabaseHandler db = new DatabaseHandler();
//        db.getDbConnection();
//        createUserCatalog ("user1");
    }

    public  void getDbConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5435/cloud","postgres","postgrespass");
            System.out.println("Подключился");
            statement = dbConnection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                                    + "id SERIAL PRIMARY KEY NOT NULL ,"
                                    + "login TEXT NOT NULL,"
                                    + "password TEXT NOT NULL)");
//            statement.executeUpdate("INSERT INTO users (login, password) VALUES ('user2', 'pass2');");
            dbConnection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createUserCatalog (String login) {
        Path path = Paths.get("NetworkStorage/NSServer/cloud");
        System.out.println(path);
//        File dir = new File (path.toAbsolutePath().toString());
        System.out.println(path.toAbsolutePath().toString());
        new File(path + "/" + login).mkdirs();
        path = Paths.get(path + "/" + login);
        System.out.println(path);
//    public void signUpUser (String login, String password) {
//        String insert =
//    }
    }
}
