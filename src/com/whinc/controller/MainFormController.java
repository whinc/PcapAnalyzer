package com.whinc.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;

/**
 * Created by Administrator on 2016/3/2.
 */
public class MainFormController {

    @FXML
    protected void handlerButtonClick() {
        Dialog<Object> dialog = new Dialog<>();
        dialog.setContentText("hello");
        dialog.showAndWait();
    }
}
