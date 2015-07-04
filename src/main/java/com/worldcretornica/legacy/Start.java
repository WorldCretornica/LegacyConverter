package com.worldcretornica.legacy;

import com.worldcretornica.legacy.storage.PlotMeMySQLConnector;
import com.worldcretornica.legacy.storage.SQLiteConnector;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Start {
    /**
     * @param args the command line arguments
     */
    public static Logger logger = Logger.getLogger("LegacyConverter");
    public static void main(String[] args) {
        FileHandler fh = null;
        try {
            fh = new FileHandler("legacyConverter.log");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        logger.addHandler(fh);
        logger.setLevel(Level.ALL);
        if (GraphicsEnvironment.isHeadless() || args.length != 0) {
            logger.config("Is Headless: " + GraphicsEnvironment.isHeadless());
            logger.log(Level.FINEST, Arrays.toString(args));
            if (args.length == 2) {
                if ("sqlite".equalsIgnoreCase(args[0])) {
                    File sqlfileHL = new File(args[1]);
                    SQLiteConnector sqLiteConnector = new SQLiteConnector(sqlfileHL);
                    sqLiteConnector.start();

                }
            } else if (args.length == 4) {
                if ("mysql".equalsIgnoreCase(args[0])) {
                    PlotMeMySQLConnector mySQLConnector = new PlotMeMySQLConnector(args[1], args[2], args[3]);
                    mySQLConnector.start();
                }
            } else {
                logger.info("Invalid Syntax. The here are two examples of the commands you can run: ");
                logger.info("java LegacyConverter sqlite C:\\Users\\Matthew\\Server\\plugins\\PlotMe\\plots.db");
                logger.info("java LegacyConverter mysql jdbc:mysql://localhost:3306/minecraft username password");
            }
        } else {
            GUIStart.launch(GUIStart.class, args);
        }
    }
}
