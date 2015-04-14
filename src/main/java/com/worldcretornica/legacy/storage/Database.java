package com.worldcretornica.legacy.storage;


import com.worldcretornica.legacy.UUIDFetcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public abstract class Database {

    Connection connection;

    /**
     * Closes the connecection to the database.
     * This will not close the connection if the connection is null.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
            }
        }
    }

    protected abstract Connection startConnection();

    /**
     * The database connection
     * @return the connection to the database
     */
    Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                return startConnection();
            }
        } catch (SQLException e) {
            return null;
        }
        return connection;
    }

    public abstract void createTables();

    public void legacyConverter() {
        Runnable legacy = (new Runnable() {
            @Override
            public void run() {
                if (tableExists("plotmePlots")) {
                    try (Statement statement = legacyConnection().createStatement()) {
                        try (ResultSet resultSet = statement.executeQuery("SELECT * FROM plotmePlots")) {
                            while (resultSet.next()) {
                                try (PreparedStatement migrateStatement = getConnection().prepareStatement(
                                        "INSERT INTO plotmecore_plots(plotX, plotZ, world, ownerID, owner, biome, finished, finishedDate, "
                                                + "forSale, price, protected, expiredDate, topX, topZ, bottomX, bottomZ) VALUES (?,"
                                                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                                    int idX = resultSet.getInt("idX");
                                    migrateStatement.setInt(1, idX);
                                    int idZ = resultSet.getInt("idZ");
                                    migrateStatement.setInt(2, idZ);
                                    String world = resultSet.getString("world");
                                    migrateStatement.setString(3, world);
                                    byte[] ownerBytesId = resultSet.getBytes("ownerId");
                                    migrateStatement.setString(4, UUIDFetcher.fromBytes(ownerBytesId).toString());
                                    migrateStatement.setString(5, resultSet.getString("owner"));
                                    migrateStatement.setString(6, resultSet.getString("biome"));
                                    migrateStatement.setBoolean(7, resultSet.getBoolean("finished"));
                                    migrateStatement.setString(8, resultSet.getString("finisheddate"));
                                    migrateStatement.setBoolean(9, resultSet.getBoolean("forsale"));
                                    migrateStatement.setInt(10, resultSet.getInt("customprice"));
                                    migrateStatement.setBoolean(11, resultSet.getBoolean("protected"));
                                    migrateStatement.setDate(12, resultSet.getDate("expireddate"));
                                    migrateStatement.setInt(13, resultSet.getInt("topX"));
                                    migrateStatement.setInt(14, resultSet.getInt("topZ"));
                                    migrateStatement.setInt(15, resultSet.getInt("bottomX"));

                                    try (ResultSet executePlots = migrateStatement.executeQuery()) {
                                        int internalPlotID = executePlots.getInt("id");
                                        if (tableExists("plotmeAllowed")) {
                                            try (ResultSet resultSet1 = statement
                                                    .executeQuery("SELECT * FROM plotmeAllowed WHERE idX = " + idX
                                                            + " AND idZ = " + idZ + " AND world = " + world)) {
                                                UUID pId = UUIDFetcher.getUUIDOf(resultSet1.getString("player"));
                                                String player = null;
                                                if (resultSet1.getString("player").equalsIgnoreCase("*")) {
                                                    player = resultSet1.getString("player");
                                                } else if (pId != null) {
                                                    player = pId.toString();
                                                }
                                                if (player != null) {
                                                    try (PreparedStatement migrateStatement2 = getConnection()
                                                            .prepareStatement("INSERT INTO "
                                                                    + "plotmecore_allowed (plot_id, player) VALUES (?,?)")) {
                                                        migrateStatement2.setInt(1, internalPlotID);
                                                        migrateStatement2.setString(2, player);
                                                        migrateStatement2.execute();
                                                    }
                                                }
                                            }

                                        }
                                        if (tableExists("plotmeDenied")) {
                                            try (ResultSet resultSet2 = statement
                                                    .executeQuery("SELECT * FROM plotmeAllowed WHERE idX = " + idX
                                                            + " AND idZ = " + idZ + " AND world = " + world)) {
                                                UUID pId = UUIDFetcher.getUUIDOf(resultSet2.getString("player"));
                                                String player = null;
                                                if (resultSet2.getString("player").equalsIgnoreCase("*")) {
                                                    player = resultSet2.getString("player");
                                                } else if (pId != null) {
                                                    player = pId.toString();
                                                }
                                                if (player != null) {
                                                    try (PreparedStatement migrateStatement3 = getConnection()
                                                            .prepareStatement("INSERT INTO "
                                                                    + "plotmecore_denied (plot_id, player) VALUES (?,?)")) {
                                                        migrateStatement3.setInt(1, internalPlotID);
                                                        migrateStatement3.setString(2, player);
                                                        migrateStatement3.execute();
                                                    }
                                                }
                                            }
                                        }
                                        if (tableExists("plotmeMetadata")) {
                                            try (ResultSet resultSet3 = statement
                                                    .executeQuery("SELECT * FROM plotmeMetadata WHERE idX = " + idX
                                                            + " AND idZ = " + idZ + " AND world = " + world)) {
                                                try (PreparedStatement migrateStatement3 = getConnection().prepareStatement("INSERT INTO "
                                                        + "plotmecore_metadata (plot_id, pluginName, propertyName, propertyValue) VALUES "
                                                        + "(?,?,"
                                                        + "?,?)")) {
                                                    migrateStatement3.setInt(1, internalPlotID);
                                                    migrateStatement3.setString(2, resultSet3.getString("pluginname"));
                                                    migrateStatement3.setString(3, resultSet3.getString("propertyname"));
                                                    migrateStatement3.setString(4, resultSet3.getString("propertyvalue"));
                                                    migrateStatement3.execute();
                                                }
                                            }
                                        }
                                        getConnection().commit();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        legacyConnection().commit();
                        getConnection().commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        );
        legacy.run();
    }

    protected abstract Connection legacyConnection();


    abstract boolean tableExists(String name);

}
