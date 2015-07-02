package com.worldcretornica.legacy.storage;


import com.worldcretornica.legacy.Plot;
import com.worldcretornica.legacy.PlotId;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public abstract class Database {

    Connection connection;
    long nextPlotId;

    public Database() {
    }

    /**
     * Closes the connecection to the database.
     * This will not close the connection if the connection is null.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract Connection startConnection();

    public void start() {
        long start = System.nanoTime();
        startConnection();
        createTables();
        legacyConverter();
        System.out.println(System.nanoTime() - start);
        System.out.println("FINISHED!");
        closeConnection();
    }

    public abstract void createTables();

    public void legacyConverter() {
        try (Statement slstatement = connection.createStatement()) {
            try (ResultSet setPlots = slstatement.executeQuery("SELECT * FROM plotmePlots")) {
                while (setPlots.next()) {
                    PlotId id = new PlotId(setPlots.getInt("idX"), setPlots.getInt("idZ"));
                    String owner = setPlots.getString("owner");
                    String world = setPlots.getString("world").toLowerCase();
                    int topX = setPlots.getInt("topX");
                    int bottomX = setPlots.getInt("bottomX");
                    int topZ = setPlots.getInt("topZ");
                    int bottomZ = setPlots.getInt("bottomZ");
                    String biome = setPlots.getString("biome");
                    Date expireddate = setPlots.getDate("expireddate");
                    boolean finished = setPlots.getBoolean("finished");
                    HashMap<String, Plot.AccessLevel> allowed = new HashMap<>();
                    HashSet<String> denied = new HashSet<>();
                    double price = setPlots.getDouble("customprice");
                    boolean forsale = setPlots.getBoolean("forsale");
                    String finisheddate = setPlots.getString("finisheddate");
                    boolean protect = setPlots.getBoolean("protected");
                    HashMap<String, Map<String, String>> metadata = new HashMap<>();

                    byte[] byOwner = setPlots.getBytes("ownerId");
                    if (byOwner != null) {
                        UUID ownerId = fromBytes(byOwner);
                        try (Statement slAllowed = connection.createStatement(); ResultSet setAllowed = slAllowed.executeQuery(
                                "SELECT * FROM plotmeAllowed WHERE idX = '" + id.getX() + "' AND idZ = '" + id.getZ() + "' AND world = '" + world
                                        + "'")) {
                            while (setAllowed.next()) {
                                byte[] byPlayerId = setAllowed.getBytes("playerid");
                                if (setAllowed.getString("player").equalsIgnoreCase("*")) {
                                    allowed.put("*", Plot.AccessLevel.ALLOWED);
                                }
                                if (byPlayerId != null) {
                                    allowed.put(fromBytes(byPlayerId).toString(), Plot.AccessLevel.ALLOWED);
                                }
                            }
                        }
                        try (Statement slDenied = connection.createStatement();
                                ResultSet setDenied = slDenied.executeQuery("SELECT * FROM plotmeDenied WHERE idX = '" + id.getX() +
                                        "' AND idZ = '" + id.getZ() + "' AND world = '" + world + "'")) {
                            while (setDenied.next()) {
                                byte[] byPlayerId = setDenied.getBytes("playerid");
                                if (setDenied.getString("player").equalsIgnoreCase("*")) {
                                    denied.add("*");
                                }
                                if (byPlayerId != null) {
                                    denied.add(fromBytes(byPlayerId).toString());
                                }
                            }
                        }
                        try (Statement slMetadata = connection.createStatement(); ResultSet setMetadata =
                                slMetadata
                                        .executeQuery("SELECT pluginname, propertyname, propertyvalue FROM plotmeMetadata WHERE idX = '" + id.getX() +
                                                "' AND idZ = '" + id.getZ() + "' AND world = '" + world + "'")) {
                            while (setMetadata.next()) {
                                String pluginname = setMetadata.getString("pluginname");
                                String propertyname = setMetadata.getString("propertyname");
                                String propertyvalue = setMetadata.getString("propertyvalue");
                                if (!metadata.containsKey(pluginname)) {
                                    metadata.put(pluginname, new HashMap<>());
                                }
                                metadata.get(pluginname).put(propertyname, propertyvalue);
                            }

                        }
                        Plot plot =
                                new Plot(nextPlotId, owner, ownerId, world, biome, expireddate, allowed, denied, id, price, forsale, finished,
                                        finisheddate, protect, metadata, topX, topZ, bottomX, bottomZ,
                                        new SimpleDateFormat("yyyy-MM-dd").format(
                                                Calendar.getInstance().getTime()));
                        addPlot(plot);
                        incrementNextPlotId();

                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void incrementNextPlotId() {
        this.setNextPlotId(this.nextPlotId + 1);
    }

    public void setNextPlotId(long id) {
        this.nextPlotId = id;

        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM plotmecore_nextid;");
            statement.execute("INSERT INTO plotmecore_nextid VALUES (" + id + ");");
        } catch (SQLException e) {
        }
    }

    public void addPlot(Plot plot) {
        System.out.println("Plot: " + plot.getId().getID() + " World: " + plot.getWorld().toLowerCase());
        System.out.println("Owner: " + plot.getOwnerId() + "(" + plot.getOwner() + ")");
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO plotmecore_plots(plot_id,plotX, plotZ, world, ownerID, owner, biome, finished, finishedDate, forSale, price, protected, "
                        + "expiredDate, topX, topZ, bottomX, bottomZ, plotLikes, createdDate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            ps.setLong(1, plot.getInternalID());
            ps.setInt(2, plot.getId().getX());
            ps.setInt(3, plot.getId().getZ());
            ps.setString(4, plot.getWorld().toLowerCase());
            ps.setString(5, plot.getOwnerId().toString());
            ps.setString(6, plot.getOwner());
            ps.setString(7, plot.getBiome());
            ps.setBoolean(8, plot.isFinished());
            ps.setString(9, plot.getFinishedDate());
            ps.setBoolean(10, plot.isForSale());
            ps.setDouble(11, plot.getPrice());
            ps.setBoolean(12, plot.isProtected());
            ps.setDate(13, plot.getExpiredDate());
            ps.setInt(14, plot.getTopX());
            ps.setInt(15, plot.getTopZ());
            ps.setInt(16, plot.getBottomX());
            ps.setInt(17, plot.getBottomZ());
            ps.setInt(18, 0);
            ps.setString(19, plot.getCreatedDate());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();

        }
        for (String allowed : plot.getMembers().keySet()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO plotmecore_allowed(plot_id, player, access) VALUES (?,?,?)")) {
                ps.setLong(1, plot.getInternalID());
                ps.setString(2, allowed);
                ps.setInt(3, Plot.AccessLevel.ALLOWED.getLevel());
                ps.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (String denied : plot.getDenied()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO plotmecore_denied(plot_id, player) VALUES (?,?)")) {
                ps.setLong(1, plot.getInternalID());
                ps.setString(2, denied);
                ps.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Map<String, Map<String, String>> metadata = plot.getAllPlotProperties();
        for (String pluginname : metadata.keySet()) {
            Map<String, String> pluginproperties = metadata.get(pluginname);
            for (String propertyname : pluginproperties.keySet()) {
                //Plots
                try (PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO plotmecore_metadata(plot_id, pluginName, propertyName, propertyValue) VALUES(?,?,?,?)")) {
                    ps.setLong(1, plot.getInternalID());
                    ps.setString(2, pluginname);
                    ps.setString(3, propertyname);
                    ps.setString(4, pluginproperties.get(propertyname));
                    ps.executeUpdate();
                    connection.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public UUID fromBytes(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: " + array.length);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        long mostSignificant = byteBuffer.getLong();
        long leastSignificant = byteBuffer.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }


}
