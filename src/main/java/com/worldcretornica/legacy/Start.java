/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.worldcretornica.legacy;

import com.worldcretornica.legacy.storage.PlotMeMySQLConnector;
import com.worldcretornica.legacy.storage.SQLiteConnector;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

/**
 *
 * @author Matthew
 */
public class Start extends Application {

    private File sqlfile = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("PlotMe Legacy Converter");
        Tab sqlLiteTab = new Tab("SQLite");
        Tab mySQLTab = new Tab("MySQL");
        TabPane tabPane = new TabPane(sqlLiteTab, mySQLTab);
        AnchorPane root = new AnchorPane(tabPane);
        stage.setScene(new Scene(root));
        sqlLiteTab.setContent(plotMeSQLiteConverter(stage));
        mySQLTab.setContent(plotMeMySQLConverter(stage));
        stage.show();
        stage.sizeToScene();
    }

    private Node plotMeMySQLConverter(Stage stage) {
        GridPane pane = new GridPane();
        final Label urlLabel = new Label("URL");
        final Label portLabel = new Label("Port");
        final Label usernameLabel = new Label("Username");
        final Label passwordLabel = new Label("Password");
        final Label databaseLabel = new Label("Database");
        final TextField url = new TextField("localhost");
        final TextField port = new TextField("3306");
        final TextField username = new TextField("root");
        final PasswordField password = new PasswordField();
        final TextField database = new TextField("minecraft");
        final Button convert = new Button("Convert");
        convert.setOnAction(event -> {
            if (url.getText() != null && !url.getText().isEmpty() && port.getText() != null && !port.getText().isEmpty() && password.getText() != null
                    && !password.getText().isEmpty() && database.getText() != null && !database.getText().isEmpty()) {
                Task<Void> task = new Task<Void>() {

                    @Override protected Void call() throws Exception {
                        String port_2 = port.getText();
                        int port_1 = Integer.parseInt(port_2);
                        PlotMeMySQLConnector mySQLConnector =
                                new PlotMeMySQLConnector(url.getText(), port_1, database.getText(), username.getText(), password
                                        .getText(), stage);
                        mySQLConnector.start();
                        return null;
                    }
                };
                new Thread(task).start();
                task.setOnRunning(event1 -> {
                    beginConvertNotification(stage);
                });
                task.setOnCancelled(event1 -> {
                    finishedNotification(stage);
                });
                task.setOnSucceeded(event1 -> {
                    finishedNotification(stage);
                });

            }
        });
        pane.setAlignment(Pos.CENTER);
        convert.setAlignment(Pos.CENTER);
        pane.add(urlLabel, 0, 0);
        pane.add(url, 1, 0);
        pane.add(portLabel, 0, 1);
        pane.add(port, 1, 1);
        pane.add(databaseLabel, 0, 2);
        pane.add(database, 1, 2);
        pane.add(usernameLabel, 0, 3);
        pane.add(username, 1, 3);
        pane.add(passwordLabel, 0, 4);
        pane.add(password, 1, 4);
        pane.add(convert, 0, 5);
        return pane;
    }

    private void finishedNotification(Stage stage) {
        Stage stage1 = new Stage();
        stage1.initModality(Modality.WINDOW_MODAL);
        stage1.initOwner(stage);
        GridPane root = new GridPane();
        root.add(new Label("Finished Converting Database. You can now close the application and run your server."), 0, 0);
        Button ok = new Button("Ok");
        root.add(ok, 0, 1);
        ok.setOnAction(event1 -> {
            stage1.close();
        });

        Scene value = new Scene(root);
        stage1.setScene(value);
        stage1.show();
        stage1.sizeToScene();

    }

    private Pane plotMeSQLiteConverter(final Stage stage) {
        GridPane pane = new GridPane();
        final Button filePicker = new Button("Select PlotMe Database");
        final Button convert = new Button("Convert");
        convert.setDisable(true);
        filePicker.setText("Pick a PlotMe database file");
        filePicker.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Database files (*.db)", "*.db");
            fileChooser.getExtensionFilters().add(extFilter);
            // Show open file dialog
            sqlfile = fileChooser.showOpenDialog(stage);
            if (sqlfile != null) {
                convert.setDisable(false);
            }
        });
        convert.setOnAction(event -> {
            if (sqlfile != null) {
                Task<Void> task = new Task<Void>() {

                    @Override protected Void call() throws Exception {
                        SQLiteConnector sqLiteConnector = new SQLiteConnector(sqlfile, stage);
                        sqLiteConnector.start();
                        return null;
                    }

                };
                new Thread(task).start();
                task.setOnRunning(event1 -> {
                    beginConvertNotification(stage);
                });
                task.setOnCancelled(event1 -> {
                    finishedNotification(stage);
                });
                task.setOnSucceeded(event1 -> {
                    finishedNotification(stage);
                });
            }
        });
        pane.setAlignment(Pos.CENTER);
        filePicker.setAlignment(Pos.CENTER);
        convert.setAlignment(Pos.CENTER);
        pane.add(filePicker, 0, 0);
        pane.add(convert, 0, 1);
        return pane;
    }

    private void beginConvertNotification(Stage stage) {
        Stage stage1 = new Stage();
        stage1.initModality(Modality.WINDOW_MODAL);
        stage1.initOwner(stage);
        GridPane root = new GridPane();
        root.add(new Label("Now Converting Database. Another popup will appear when done."), 0, 0);
        Button ok = new Button("Ok");
        root.add(ok, 0, 1);
        ok.setOnAction(event1 -> stage1.close());

        Scene value = new Scene(root);
        stage1.setScene(value);
        stage1.show();
        stage1.sizeToScene();
    }

}
