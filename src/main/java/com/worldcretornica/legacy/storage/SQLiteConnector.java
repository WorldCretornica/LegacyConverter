package com.worldcretornica.legacy.storage;

import com.worldcretornica.legacy.Start;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnector extends Database {

    private final String legacyURL;

    public SQLiteConnector(File coreDB) {
        this.legacyURL = "jdbc:sqlite:" + coreDB.getAbsolutePath();
        startConnection();
        createTables();
    }

    /**
     * Establish a connection to the plotme database
     * @return connection established
     */
    @Override
    public Connection startConnection() {
        try {
            connection = new SQLiteConfig().createConnection(legacyURL);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            Start.logger.severe(e.getMessage());
        }
        return null;
    }

    @Override public void createTables() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plotmecore_nextplotid (nextid INT(15));");
            //MySQL specific plot table creation.
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_plots` ("
                    + "`plot_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "`plotX` INTEGER NOT NULL DEFAULT '0',"
                    + "`plotZ` INTEGER NOT NULL DEFAULT '0',"
                    + "`world` VARCHAR(32) NOT NULL DEFAULT 'world',"
                    + "`ownerID` VARCHAR(50) NOT NULL DEFAULT '473cd4a7927741fabdbbdf98df801776',"
                    + "`owner` VARCHAR(32) NOT NULL DEFAULT 'MBon29',"
                    + "`biome` VARCHAR(50) NOT NULL DEFAULT 'PLAINS',"
                    + "`finished` BOOLEAN NOT NULL DEFAULT '0',"
                    + "`finishedDate` VARCHAR(20) DEFAULT NULL,"
                    + "`createdDate` VARCHAR(20) DEFAULT 'Unknown',"
                    + "`forSale` BOOLEAN NOT NULL DEFAULT '0',"
                    + "`price` DOUBLE NOT NULL DEFAULT '0',"
                    + "`protected` BOOLEAN NOT NULL DEFAULT '0',"
                    + "`expiredDate` DATETIME DEFAULT NULL,"
                    + "`topX` INTEGER NOT NULL DEFAULT '0',"
                    + "`topZ` INTEGER NOT NULL DEFAULT '0',"
                    + "`bottomX` INTEGER NOT NULL DEFAULT '0',"
                    + "`bottomZ` INTEGER NOT NULL DEFAULT '0',"
                    + "`plotName` VARCHAR(32) DEFAULT NULL UNIQUE,"
                    + "`plotLikes` INTEGER NOT NULL DEFAULT '0',"
                    + "`homeX` INTEGER NOT NULL DEFAULT '0',"
                    + "`homeY` INTEGER NOT NULL DEFAULT '0',"
                    + "`homeZ` INTEGER NOT NULL DEFAULT '0',"
                    + "`homeName` VARCHAR(32) DEFAULT NULL"
                    + ");");
            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS `plotLocation` ON plotmecore_plots(plotx,plotz,world);");
            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS `playerHome` ON plotmecore_plots(ownerid,homename);");
            connection.commit();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_denied` ("
                    + "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "`plot_id` INTEGER NOT NULL DEFAULT '0',"
                    + "`player` VARCHAR(50) NOT NULL DEFAULT '*'"
                    + ");");
            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS `denied` ON plotmecore_denied(plot_id,player)");
            connection.commit();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_allowed` ("
                    + "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "`plot_id` INTEGER NOT NULL DEFAULT '0',"
                    + "`player` VARCHAR(50) NOT NULL DEFAULT '*',"
                    + "`access` INTEGER NOT NULL DEFAULT '1'"
                    + ");");
            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS `allowed` ON plotmecore_allowed(plot_id,player)");
            connection.commit();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plotmecore_likes ("
                    + "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "`plot_id` INTEGER NOT NULL DEFAULT '0',"
                    + "`player` VARCHAR(50) NOT NULL DEFAULT '*'"
                    + ");");
            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS `likes` ON plotmecore_likes(plot_id,player)");
            connection.commit();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_metadata` ("
                    + "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "`plot_id` INTEGER NOT NULL DEFAULT '0',"
                    + "`pluginName` VARCHAR(100) NOT NULL,"
                    + "`propertyName` VARCHAR(100) NOT NULL,"
                    + "`propertyValue` VARCHAR(255) DEFAULT NULL"
                    + ");");
            connection.commit();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_nextid` (`nextId` INTEGER NOT NULL DEFAULT '0');");
            connection.commit();
            try (ResultSet results = statement.executeQuery("SELECT * FROM plotmecore_nextid;")) {
                if (!results.next()) {
                    statement.execute("INSERT INTO plotmecore_nextid VALUES(1);");
                    this.nextPlotId = 1;
                } else {
                    this.nextPlotId = results.getLong("nextid");
                }
            }
            connection.commit();
        } catch (SQLException e) {
            Start.logger.severe(e.getMessage());
        }
    }
}
