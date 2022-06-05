package ru.geekbrains.jt.chat.server.core;

import java.sql.*;

public class SqlClient {
    private static final String REQUEST_FOR_REG =
            "insert into users (login, password, nickname) values ('%s', '%s', '%s')";
    private static Connection connection;
    private static Statement statement;

    synchronized static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat-server/clients-db.sqlite");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static void disconnect() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    synchronized static String getNick(String login, String password) {
        String query = String.format(
                "select nickname from users where login='%s' and password='%s'",
                login, password);
        try (ResultSet set = statement.executeQuery(query)) {
            if (set.next())
                return set.getString("nickname");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    synchronized static void regNewUser(String login, String password, String nickname) throws SQLException{
        String query = String.format(REQUEST_FOR_REG, login, password, nickname);
        int count = statement.executeUpdate(query);
        System.out.printf("Updated %d rows\n", count);
    }
}
