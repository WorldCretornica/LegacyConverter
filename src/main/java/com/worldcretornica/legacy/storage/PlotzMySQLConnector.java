package com.worldcretornica.legacy.storage;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.worldcretornica.legacy.Plot;
import com.worldcretornica.legacy.PlotId;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class PlotzMySQLConnector extends Database {

    private final String hostname;
    private final String password;
    private final String port;
    private final String user;
    private final String database;

    public PlotzMySQLConnector(String hostname, char[] password, String port, String user, String database) {
        this.hostname = hostname;
        this.port = port;
        this.user = user;
        this.database = database;
        this.password = String.copyValueOf(password);
        startConnection();
    }

    public static PlotId weirdPlotZIDCalc(final int i) {
        if (i == 0) {
            return new PlotId();
        }
        int j = (int) Math.floor(Math.sqrt(i));
        if (j % 2 == 0) {
            --j;
        }
        final int w = (int) Math.ceil(j / 2.0) * 2 + 1;
        int x = 0;
        int z = -(int) Math.ceil(j / 2.0);
        int t = j * j;
        if (t + (int) Math.ceil(j / 2.0) <= i) {
            x -= (int) Math.ceil(j / 2.0);
            t += (int) Math.ceil(j / 2.0);
            if (t + w - 1 <= i) {
                z += w - 1;
                t += w - 1;
                if (t + w - 1 <= i) {
                    x += w - 1;
                    t += w - 1;
                    if (t + w - 1 <= i) {
                        z -= w - 1;
                        t += w - 1;
                        if (t + (int) Math.ceil(j / 2.0) <= i) {
                            x -= (int) Math.ceil(j / 2.0);
                        } else {
                            x -= i - t;
                        }
                    } else {
                        z -= i - t;
                    }
                } else {
                    x += i - t;
                }
            } else {
                z += i - t;
            }
        } else {
            x -= i - t;
        }
        return new PlotId(x, z);
    }

    @Override protected Connection startConnection() {
        try {
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            this.connection = DriverManager
                    .getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true&", this.user,
                            this.password);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override public void createTables() {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                //MySQL specific plot table creation.
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `plotmecore_plots` ("
                        + "`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                        + "`plotX` INTEGER NOT NULL,"
                        + "`plotZ` INTEGER NOT NULL,"
                        + "`world` VARCHAR(32) NOT NULL,"
                        + "`ownerID` VARCHAR(50) NOT NULL,"
                        + "`owner` VARCHAR(32) NOT NULL,"
                        + "`biome` VARCHAR(50) NOT NULL DEFAULT 'PLAINS',"
                        + "`finished` BOOLEAN NOT NULL DEFAULT '0',"
                        + "`finishedDate` VARCHAR(20) DEFAULT NULL,"
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void convert() {
        int internalId = 0;
        try (Statement statement = getConnection().createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM plots;")) {
                while (resultSet.next()) {
                    String pid = resultSet.getString("id");
                    String[] id = pid.split("_");
                    PlotId plotId;
                    String world;
                    if (id.length == 3) {
                        plotId = new PlotId(Integer.parseInt(id[0]), Integer.parseInt(id[1]));
                        world = id[2].toLowerCase();
                    } else {
                        plotId = weirdPlotZIDCalc(Integer.parseInt(id[0]));
                        world = id[1].toLowerCase();
                    }
                    Plot plot = new Plot(null, UUID.fromString(resultSet.getString("owner")), world, plotId, 0, 0, 0, 0);
                    plot.setInternalID(internalId);
                    plot.setBiome(resultSet.getString("biome"));
                    if (resultSet.getInt("done") != 0) {
                        plot.setFinished(true);
                    }
                    plot.setForSale(resultSet.getInt("forsale") != 0);
                    internalId++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override boolean tableExists(String name) {
        return false;
    }
}
