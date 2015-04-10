package com.worldcretornica.legacy;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PlotMeDatabaseFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        return f.getName().equalsIgnoreCase("plots.db");
    }

    @Override
    public String getDescription() {
        return "Database files.";
    }
}
