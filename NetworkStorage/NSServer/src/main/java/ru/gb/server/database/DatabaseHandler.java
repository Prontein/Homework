package ru.gb.server.database;

import ru.gb.server.util.PropertyUtils;
import java.sql.*;


public class DatabaseHandler {
    private static Connection dbConnection;

    public static void getDbConnection() {
        try {
            dbConnection = DriverManager.getConnection(PropertyUtils.getProperties("URL"),
                    PropertyUtils.getProperties("USER"), PropertyUtils.getProperties("PASSWORD"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean createNewUser (String login, String password) {
        boolean isRegistrationAccept;

        getDbConnection();
        isRegistrationAccept = createPrepStatementNewUser(login,password);
        disconnect();
        return isRegistrationAccept;
    }

    public static boolean authorizationUser (String login, String password) {
        boolean isUserExist;

        getDbConnection();
        isUserExist = !isUserExist(login,password);
        disconnect();
        return isUserExist;
    }

    private static boolean createPrepStatementNewUser(String login, String password) {
        boolean isRegistrationAccept = true;

        try (PreparedStatement ps = dbConnection.prepareStatement(PropertyUtils.getProperties("SQL_INSERT"))) {
            ps.setString(1, login);
            ps.setString(2, password);

            if (isUserExist(login,password)) {
                ps.execute();
                isRegistrationAccept = true;
            } else {
                isRegistrationAccept = false;;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isRegistrationAccept;
    }

    private static boolean isUserExist (String login, String password) {
        try (Statement statement = dbConnection.createStatement();
             ResultSet rs = statement.executeQuery(PropertyUtils.getProperties("SQL_SELECT"))) {

            while (rs.next()) {
                if (rs.getString("login").equals(login) && rs.getString("password").equals(password)) return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    private static void disconnect () {
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
