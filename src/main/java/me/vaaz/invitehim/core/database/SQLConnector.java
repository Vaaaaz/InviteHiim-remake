package me.vaaz.invitehim.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnector {

    private final String dbType;
    private Connection connection;
    private String file;

    private String host;
    private String database;
    private String username;
    private String password;

    public SQLConnector(String dbType, String file) {
        this.dbType = dbType;
        this.file = file;
    }

    public SQLConnector(String dbType, String host, String database, String username, String password) {
        this.dbType = dbType;
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /*
    Connect to SQLite or MySQL depending on specified type
     */
    public boolean connect() {
        if ("mysql".equalsIgnoreCase(dbType)) {
            try {
                if (host == null || database == null || username == null || password == null) {
                    throw new NullPointerException("host or database or username or password of mysql connection can't be null.");
                }

                this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?autoReconnect=true", username, password);
                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Class.forName("org.sqlite.JDBC");
                this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
                return true;
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
    Disconnect from current Database
     */
    public void disconnect() {
        if (connected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
    Get connection
     */
    public Connection connection() {
        return connection;
    }

    /*
    Returns if is currently connected or not
     */
    public boolean connected() {
        return connection != null;
    }

    /*
    Get the type of database that currently is connected
     */
    public String type() {
        return dbType;
    }

    /*
    Automatically builds a SQLExecutor instance
     */
    public SQLExecutor executor() {
        return new SQLExecutor(connection);
    }

}
