package no.hiof.webframework.Repository;

import no.hiof.webframework.Interface.RepositoryConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection implements RepositoryConnection {
    private String url;
    private String username;
    private String password;
    private Connection connection;

/**
 *  Constructs a new SqlConnection object with the given URL, username, and password parameters.
 *  @param url The URL of the SQL database.
 *  @param username The username for accessing the SQL database.
 *  @param password The password for accessing the SQL database.
 */
    public SqlConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public void connect() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }

    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database.");
            }
        } catch (SQLException e) {
            System.err.println("Error disconnecting from database: " + e.getMessage());
        }
    }

}
