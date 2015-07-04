package com.worldcretornica.legacy.storage;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.worldcretornica.legacy.Start;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PlotMeMySQLConnector extends Database {

    private final String url;
    private final String userName;
    private final String password;

    public PlotMeMySQLConnector(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        startConnection();
        createTables();
    }

    @Override
    public Connection startConnection() {
        try {
            Start.logger.info("connection url: " + url + " username: " + userName + " password: " + password);
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setUrl(url);
            connection = mysqlDataSource.getConnection(userName, password);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            Start.logger.info("PlotMe could not establish a connection to the MySQL database:");
            Start.logger.severe("SQLException: " + e.getMessage());
            Start.logger.severe("SQLState: " + e.getSQLState());
            Start.logger.severe("VendorError: " + e.getErrorCode());
            return null;
        }
    }

    @Override
    public void createTables() {
        try (Statement statement = connection.createStatement()) {
            //MySQL specific plot table creation.
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_plots` ("
                    + "`plot_id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "`plotX` INTEGER NOT NULL,"
                    + "`plotZ` INTEGER NOT NULL,"
                    + "`world` VARCHAR(32) NOT NULL,"
                    + "`ownerID` VARCHAR(50) NOT NULL,"
                    + "`owner` VARCHAR(32) NOT NULL,"
                    + "`biome` VARCHAR(50) NOT NULL DEFAULT 'PLAINS',"
                    + "`finished` BOOLEAN NOT NULL DEFAULT '0',"
                    + "`finishedDate` VARCHAR(20) DEFAULT NULL,"
                    + "`createdDate` VARCHAR(20) DEFAULT 'Unknown',"
                    + "`forSale` BOOLEAN NOT NULL DEFAULT '0',"
                    + "`price` DOUBLE NOT NULL DEFAULT '0',"
                    + "`protected` BOOLEAN NOT NULL DEFAULT '0',"
                    + "`expiredDate` DATETIME NULL DEFAULT NULL,"
                    + "`topX` INTEGER NOT NULL DEFAULT '0',"
                    + "`topZ` INTEGER NOT NULL DEFAULT '0',"
                    + "`bottomX` INTEGER NOT NULL DEFAULT '0',"
                    + "`bottomZ` INTEGER NOT NULL DEFAULT '0',"
                    + "`plotName` VARCHAR(32) DEFAULT NULL UNIQUE,"
                    + "`plotLikes` INTEGER NOT NULL DEFAULT '0',"
                    + "`homeX` INTEGER NOT NULL DEFAULT '0',"
                    + "`homeY` INTEGER NOT NULL DEFAULT '0',"
                    + "`homeZ` INTEGER NOT NULL DEFAULT '0',"
                    + "`homeName` VARCHAR(32) DEFAULT NULL,"
                    + "UNIQUE KEY `plotLocation` (`plotX`,`plotZ`,`world`),"
                    + "UNIQUE KEY `playerHome` (`ownerID`(16),`homeName`)"
                    + ");");
            connection.commit();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_denied` ("
                    + "`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY ,"
                    + "`plot_id` INTEGER NOT NULL,"
                    + "`player` VARCHAR(50) NOT NULL,"
                    + "UNIQUE INDEX `allowed` (plot_id,player)"
                    + ");");
            connection.commit();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_allowed` ("
                    + "`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY ,"
                    + "`plot_id` INTEGER NOT NULL,"
                    + "`player` VARCHAR(50) NOT NULL,"
                    + "`access` INTEGER NOT NULL DEFAULT '1',"
                    + "UNIQUE INDEX `allowed` (plot_id,player)"
                    + ");");
            connection.commit();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plotmecore_likes ("
                    + "`id` INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                    + "`plot_id` INTEGER NOT NULL,"
                    + "`player` VARCHAR(50) NOT NULL,"
                    + "UNIQUE INDEX `likes` (plot_id, player)"
                    + ");");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_metadata` ("
                    + "`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "`plot_id` INTEGER NOT NULL,"
                    + "`pluginname` VARCHAR(100) NOT NULL,"
                    + "`propertyname` VARCHAR(100) NOT NULL,"
                    + "`propertyvalue` VARCHAR(255) DEFAULT NULL"
                    + ");");
            connection.commit();
        } catch (SQLException e) {
            Start.logger.severe(e.getMessage());
        }
    }

}