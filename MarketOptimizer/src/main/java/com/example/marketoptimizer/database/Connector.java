package com.example.marketoptimizer.database;

import java.io.IOException;
import java.sql.*;
import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.text.SimpleDateFormat;
import java.sql.Statement;
import java.util.Date;

/**
 * Class Connector for universal Connector
 * Methods to create/drop/update tables/database
 */
public class Connector {
    String urlName;

    public Connector() {}

    public Connector(String urlName) {
        this.urlName = urlName;
    }

    /**
     * Method to establish a connection to a database with name urlName
     * @return Connection
     */
    private Connection connection() {
        //SQLite connection
        String workingDirectory = System.getProperty("user.dir");
        String url = "jdbc:sqlite:" + workingDirectory + "\\database\\" + urlName;
        Connection conn = null;


        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException s) {
            System.out.println(s.getMessage());
        }
        return conn;
    }

    /**
     * insert new Player at first start
     * @param userID
     * @param userName
     */
    public void insertPlayer(Integer userID, String userName) {
        String sql = "INSERT INTO user(userID, userName) VALUES (?,?)";

        try (Connection conn = this.connection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            if(Objects.equals(userName, "")) { userName = "Not Used"; }
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert Orders into orders
     * @param itemName
     * @param price
     * @param date
     * @param userID
     * @throws ParseException
     */
    public void insertOrder(String itemName, long price, String date, Integer userID) throws ParseException {
        String sql = "INSERT INTO orders(itemName, price, time, userID) VALUES(?,?,?,?)";
        String replacedDate = date.replace(".", "-");

        try(Connection conn = this.connection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemName);
            pstmt.setLong(2, price);
            pstmt.setString(3, replacedDate);
            pstmt.setInt(4, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert items into items
     * @param itemName
     * @param userID
     */
    public void insertItems(String itemName, Integer userID) {
        String sql = "INSERT INTO items(itemName, userID) VALUES(?,?)";

        try(Connection conn = this.connection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemName);
            pstmt.setInt(2, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update players with new name
     * @param userID
     * @param userName
     * @throws SQLException
     */
    public void updatePlayer(Integer userID, String userName) throws SQLException {
        String sql = "UPDATE user SET userName = ? "
                + "WHERE userID = ?";
        try (Connection conn = this.connection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if(Objects.equals(userName, "")) { userName = "Not Used"; }
            pstmt.setString(1, userName);
            pstmt.setInt(2, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * delete all items with userID
     * @param userID
     */
    public void deleteItems(Integer userID) {
        String sql = "DELETE FROM items WHERE userID = ?";

        try(Connection conn = this.connection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Create Database
     * @throws SQLException
     */
    public void createDataBase() throws SQLException {
        //this.urlName = urlName + ".db";
        try (Connection conn = this.connection();) {

            if (conn != null) {
                System.out.println(conn.getMetaData().getDriverName());
            }
        }
    }

    /**
     * Create all needed tables
     * @throws SQLException
     */
    public void createTable() throws SQLException {
        // Strings for instructions to create tables
        String sqlUser = "CREATE TABLE IF NOT EXISTS user (\n"
                + "     userID INTEGER PRIMARY KEY , \n"
                + "     userName TEXT NOT NULL, \n"
                + "     orderCount INTEGER \n"
                + ");";
        String sqlItems = "CREATE TABLE IF NOT EXISTS items (\n"
                + "     itemID INTEGER PRIMARY KEY ASC, \n"
                + "     itemName TEXT NOT NULL, \n"
                + "     userID INTEGER NOT NULL \n"
                + ");";
        String sqlOrders = "CREATE TABLE IF NOT EXISTS orders (\n"
                + "     orderID INTEGER PRIMARY KEY ASC, \n"
                + "     itemName TEXT NOT NULL, \n"
                + "     price INTEGER NOT NULL, \n"
                + "     time TEXT NOT NULL, \n"
                + "     userID INTEGER NOT NULL \n"
                + ");";

        // create tables
        try (Connection conn = this.connection();
             Statement stmt = conn.createStatement()) {
            // create new table
            stmt.execute(sqlUser);
            stmt.execute(sqlItems);
            stmt.execute(sqlOrders);
        }
    }

    public void dropDataBase() {

    }

    public void dropTable() {

    }

    /**
     * check if player already exists
     * @param userID
     * @return
     */
    public String checkPlayer(Integer userID) {
        String sql = "SELECT userName "
                + "FROM user WHERE userID = ?";

        try (Connection conn = this.connection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();
            return rs.getString("userName");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Fehler";
        }
    }

    public String getLastOrder(Integer userID) {
        String sql = "SELECT itemName, time FROM orders WHERE userID = ? ORDER BY time DESC LIMIT 1";

        try(Connection conn = this.connection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            ResultSet set = pstmt.executeQuery();
            return set.getString("itemName") + "\t"
                                + set.getString("time");
        } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
        return "fail";
    }

    /**
     * extract orderCount of database
     * @param userID
     * @return 0 if no orders where found
     */
    public int getOrderCount(Integer userID) {
        String sql = "SELECT orderCount FROM user WHERE userID = ?";

        try (Connection conn = this.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            ResultSet set = pstmt.executeQuery();
            return Integer.parseInt(set.getString("orderCount"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    /**
     * Count all orders for userID and update column with new orderCount
     * @param userID
     */
    public void updateOrderCount(Integer userID) {
        String sql = "UPDATE user SET orderCount = ? WHERE userID = ?";
        String count = "SELECT COUNT(*) FROM orders WHERE userID = ?";

        try (Connection conn = this.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement pstmtCount = conn.prepareStatement(count) ) {
            // get new orderCount
            pstmtCount.setInt(1, userID);
            ResultSet set = pstmtCount.executeQuery();
            int orderCount = set.getInt(1);
            // update orderCount
            pstmt.setInt(1, orderCount);
            pstmt.setInt(2, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
