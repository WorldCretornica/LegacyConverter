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
import java.util.logging.Level;

/**
 *
 * @author Matthew
 */
public class GUIStart extends Application {

    private File sqlfile = null;

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
        final Label urlLabel = new Label("MySQL URL");
        final Label usernameLabel = new Label("Username");
        final Label passwordLabel = new Label("Password");
        final TextField url = new TextField("jdbc:mysql://localhost:3306/minecraft");
        final TextField username = new TextField("root");
        final TextField password = new TextField();
        final Button convert = new Button("Convert");
        convert.setOnAction(event -> {
            if (url.getText() != null && !url.getText().isEmpty() && password.getText() != null
                    && !password.getText().isEmpty()) {
                Task<Void> task = new Task<Void>() {

                    @Override protected Void call() throws Exception {
                        PlotMeMySQLConnector mySQLConnector =
                                new PlotMeMySQLConnector(url.getText(), username.getText(), password
                                        .getText());
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
            } else {
                Start.logger.log(Level.WARNING, "One of the text boxes was null or empty.");
            }
        });
        pane.setAlignment(Pos.CENTER);
        convert.setAlignment(Pos.CENTER);
        pane.add(urlLabel, 0, 0);
        pane.add(url, 1, 0);
        pane.add(usernameLabel, 0, 1);
        pane.add(username, 1, 1);
        pane.add(passwordLabel, 0, 2);
        pane.add(password, 1, 2);
        pane.add(convert, 0, 3);
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
                        SQLiteConnector sqLiteConnector = new SQLiteConnector(sqlfile);
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
