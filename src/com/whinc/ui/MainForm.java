package com.whinc.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created by Administrator on 2016/3/2.
 */
public class MainForm extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = getClass().getClassLoader().getResource("fxml/main_form.fxml");
        Parent root = FXMLLoader.<Parent>load(url);

        Rectangle2D rect = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, rect.getWidth() / 2.0, rect.getHeight() / 2.0);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
