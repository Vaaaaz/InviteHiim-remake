package me.vaaz.invitehim.core.database;

import lombok.SneakyThrows;
import lombok.val;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLExecutor {

    private final Connection connection;
    private final ExecutorService executorService;

    public SQLExecutor(Connection connection) {
        this.connection = connection;
        this.executorService = Executors.newWorkStealingPool();
    }

    /*
    Returns the PreparedStatement for the specified query
     */
    public PreparedStatement prepare(String query) {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Execute the PreparedStatement and automatically return the ResultSet
     */
    public ResultSet result(PreparedStatement prepared) {
        try {
            return prepared.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Create a table with specified name and values (params)
     */
    public void createTable(String tableName, String values) {
        try {
            prepare("create table if not exists `" + tableName + "`(" + values + ");").executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Get a double from Database
     */
    @SneakyThrows
    public double getDouble(String table, String uniqueID, String key) {
        PreparedStatement preparedStm = prepare("select * from `" + table + "` where id = ?");
        preparedStm.setString(1, uniqueID);
        ResultSet rSet = result(preparedStm);

        double val = 0.0;

        if (rSet.next()) {
            val = rSet.getDouble(key);
        }

        return val;
    }

    /*
    Get a String from Database
     */
    @SneakyThrows
    public String getString(String table, String uniqueID, String key) throws SQLException {
        PreparedStatement preparedStm = prepare("select * from `" + table + "` where id = ?");
        preparedStm.setString(1, uniqueID);
        ResultSet rSet = result(preparedStm);

        String val = null;

        if (rSet.next()) {
            val = rSet.getString(key);
        }

        return val;
    }


    /*
    Get a Integer from Database
     */
    @SneakyThrows
    public int getInt(String table, String uniqueID, String key) {
        PreparedStatement preparedStm = prepare("select * from `" + table + "` where id = ?");
        preparedStm.setString(1, uniqueID);
        ResultSet rSet = result(preparedStm);

        int val = 0;

        if (rSet.next()) {
            val = rSet.getInt(key);
        }

        return val;
    }


    /*
    Update an existing value in database
     */
    @SneakyThrows
    public void update(String table, String uniqueValue, String key, Object val) {
        Runnable updateAction = () -> {
            try {
                PreparedStatement preparedStm = prepare("update `" + table + "` set `" + key + "` = ? where id = ?");
                preparedStm.setObject(1, val);
                preparedStm.setString(2, uniqueValue);
                preparedStm.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }; //só perguntando, vc deixou pra tipo, a pessoa conseguir convidar varias mas qm convidou conseguir só uma né? é

        executorService.submit(updateAction);
    }

    /*
    Create the first values when player join for first time
     */
    public void playerFirstValues(String player, String playerCode, int rewardPriority) {
        val preparedStm = prepare("insert ignore into `invite_him` VALUES(?, ?, ?, ?, ?, ?)");
        try {
            preparedStm.setString(1, player);
            preparedStm.setString(2, playerCode);
            preparedStm.setString(3, null);
            preparedStm.setInt(4, 0);
            preparedStm.setInt(5, rewardPriority);
            preparedStm.setString(6, "pending");

            executorService.submit(() -> preparedStm.executeUpdate());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public String getPlayerByCode(String playerCode) {
        val preparedStm = prepare("select * from `invite_him` where code = ?");
        preparedStm.setString(1, playerCode);

        val rSet = result(preparedStm);

        if (rSet.next()) {
            return rSet.getString("id");
        } else {
            return null;
        }
    }

}

