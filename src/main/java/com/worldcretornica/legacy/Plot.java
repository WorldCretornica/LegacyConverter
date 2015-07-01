package com.worldcretornica.legacy;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class Plot {

    private final HashMap<String, Plot.AccessLevel> allowed = new HashMap<>();
    private final HashSet<String> denied = new HashSet<>();
    private final HashMap<String, Map<String, String>> metadata = new HashMap<>();
    private final int plotTopZ;
    private final int plotBottomZ;
    private final int plotBottomX;
    private final String createdDate;
    private String owner = "Unknown";
    private UUID ownerId = UUID.randomUUID();
    private String world;
    private String biome = "PLAINS";
    private Date expiredDate = null;
    private boolean finished = false;
    private PlotId id = new PlotId(0, 0);
    private double price = 0.0;
    private boolean forSale = false;
    private String finishedDate = null;
    private boolean protect = false;
    //defaults to 0 until it is saved to the database
    private long internalID = 0;
    private int plotTopX;

    public Plot(String owner, UUID uuid, String world, PlotId plotId, int bottomX, int bottomZ, int topX, int topZ) {
        setOwner(owner);
        setOwnerId(uuid);
        setWorld(world);
        setId(plotId);
        this.plotTopX = topX;
        this.plotTopZ = topZ;
        this.plotBottomX = bottomX;
        this.plotBottomZ = bottomZ;
        createdDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    public Plot(long internalID, String owner, UUID ownerId, String world, String biome, Date expiredDate,
            HashMap<String, AccessLevel> allowed,
            HashSet<String>
                    denied,
            PlotId id, double price, boolean forSale, boolean finished, String finishedDate, boolean protect,
            Map<String, Map<String, String>> metadata, int topX, int topZ, int bottomX, int bottomZ,
            String createdDate) {
        this.internalID = internalID;
        this.owner = owner;
        this.ownerId = ownerId;
        this.world = world.toLowerCase();
        this.biome = biome;
        this.expiredDate = expiredDate;
        this.finished = finished;
        this.finishedDate = finishedDate;
        this.allowed.putAll(allowed);
        this.id = id;
        this.price = price;
        this.forSale = forSale;
        this.finishedDate = finishedDate;
        this.protect = protect;
        this.denied.addAll(denied);
        this.metadata.putAll(metadata);
        this.plotTopX = topX;
        this.plotTopZ = topZ;
        this.plotBottomX = bottomX;
        this.plotBottomZ = bottomZ;
        this.createdDate = createdDate;
    }

    public String getBiome() {
        return biome;
    }

    public final void setBiome(String biome) {
        this.biome = biome;

    }

    public final String getOwner() {
        return owner;
    }

    public final void setOwner(String owner) {
        this.owner = owner;
    }

    public final UUID getOwnerId() {
        return ownerId;
    }

    public final void setOwnerId(UUID uuid) {
        ownerId = uuid;
    }

    public HashSet<String> getDenied() {
        return denied;
    }

    /**
     * A map of allowed and trusted players
     * @return allowed and trusted player map
     */
    public HashMap<String, Plot.AccessLevel> getMembers() {
        return allowed;
    }

    public final String getWorld() {
        return world.toLowerCase();
    }

    public final void setWorld(String world) {
        this.world = world.toLowerCase();
    }

    public final Date getExpiredDate() {
        return expiredDate;
    }

    public final boolean isFinished() {
        return finished;
    }

    public final void setFinished(boolean finished) {
        this.finished = finished;
        if (finished) {
            setFinishedDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        } else {
            setFinishedDate(null);
        }
    }

    public final PlotId getId() {
        return id;
    }

    public final void setId(PlotId id) {
        this.id = id;
    }

    /**
     * Retrieves the price of the plot.
     * If {@link #isForSale()} is false then this should return 0
     * @return the price of the plot
     */
    public final double getPrice() {
        return price;
    }

    /**
     * Checks if this plot is able to be sold
     * @return true if it is for sale, false otherwise
     */
    public final boolean isForSale() {
        return forSale;
    }

    /**
     * Sets if this plot can be sold or not
     * @param forSale true if it can be sold, false if it cannot be sold
     */
    public final void setForSale(boolean forSale) {
        this.forSale = forSale;

    }

    public final String getFinishedDate() {
        return finishedDate;
    }

    private void setFinishedDate(String finishedDate) {
        this.finishedDate = finishedDate;

    }

    public final boolean isProtected() {
        return protect;
    }

    public Map<String, Map<String, String>> getAllPlotProperties() {
        return metadata;
    }

    /**
     * Retrieves the unique internal id for this plot.
     * Commonly used for database lookups and debugging.
     * Normal users should not be concerned about this number nor should they need to see it.
     * @return unique internal id
     */
    public long getInternalID() {
        return internalID;
    }

    /**
     * Sets the unique internal id for this plot.
     * @param internalID unique long value
     */
    public void setInternalID(long internalID) {
        this.internalID = internalID;
    }

    public int getTopX() {
        return plotTopX;
    }

    public int getTopZ() {
        return plotTopZ;
    }

    public int getBottomX() {
        return plotBottomX;
    }

    public int getBottomZ() {
        return plotBottomZ;
    }

    public String getCreatedDate() {
        return createdDate;
    }


    public enum AccessLevel {
        ALLOWED(0),
        TRUSTED(1);

        private final int level;

        AccessLevel(int accessLevel) {
            level = accessLevel;
        }

        public static AccessLevel getAccessLevel(int level) {
            switch (level) {
                case 0:
                    return ALLOWED;
                case 1:
                    return TRUSTED;
                default:
                    return ALLOWED;
            }
        }

        public int getLevel() {
            return level;
        }
    }
}