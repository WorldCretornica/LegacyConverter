package com.worldcretornica.legacy;


public class PlotId {

    private final int x;
    private final int z;

    public PlotId(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public PlotId() {
        this.x = 0;
        this.z = 0;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public String getID() {
        return x + ";" + z;
    }

    @Override
    public String toString() {
        return getID();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof PlotId) {
            PlotId me = (PlotId) obj;
            result = this.getX() == me.getX() && this.getZ() == me.getZ();
        }
        return result;
    }

    @Override
    public int hashCode() {
        return getX() + getZ();
    }
}

