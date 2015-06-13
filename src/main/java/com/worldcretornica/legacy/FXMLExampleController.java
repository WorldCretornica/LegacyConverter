/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.worldcretornica.legacy;

import com.worldcretornica.legacy.storage.SQLiteConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author Matthew
 */
public class FXMLExampleController extends AnchorPane implements Initializable {

    public Button plotmeSQLiteFileChooser;
    public Button plotmeSQLiteConvert;
    FileChooser fileChooser = new FileChooser();

    @FXML
    private Label label;
    private File plotmeSQLiteDatabase;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        File file = fileChooser.showOpenDialog(plotmeSQLiteFileChooser.getScene().getWindow());
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("plotme database", ".db"));
        if (file != null) {
            plotmeSQLiteDatabase = file;
            plotmeSQLiteConvert.setDisable(false);
            plotmeSQLiteFileChooser.setText(file.getPath());
            plotmeSQLiteFileChooser.setDisable(true);
        }
    }

    @FXML
    private void handlePlotMeLegacySQLite(ActionEvent event) {
        SQLiteConnector sqlConnector = new SQLiteConnector(plotmeSQLiteDatabase);
        sqlConnector.legacyConverter();
    }

    @FXML
    private void handlePlotMeLegacyMySQL(ActionEvent event) {
        SQLiteConnector sqlConnector = new SQLiteConnector(plotmeSQLiteDatabase);
        sqlConnector.legacyConverter();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
