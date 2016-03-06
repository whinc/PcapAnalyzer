package com.whinc.ui;

import com.whinc.Config;
import com.whinc.controller.MainFormController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Created by Administrator on 2016/3/2.
 */
public class MainForm extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(
                Config.getResource("fxml/main_form.fxml"),
                Config.getStringResource()
        );
        Parent root = fxmlLoader.load();
        root.getStylesheets().addAll("css/main_form.css");
        MainFormController controller = fxmlLoader.<MainFormController>getController();
        controller.setStage(primaryStage);

        Rectangle2D rect = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(Config.getString("title_main_form"));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
