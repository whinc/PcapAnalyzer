package com.whinc.ui;

import com.whinc.Config;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.jnetpcap.PcapIf;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/3/4.
 */
public class OptionDialog<T> extends Dialog<T>{

    private List<PcapIf> interfaces;    // network card interface

    public OptionDialog(List<PcapIf> interfaces) {
        super();

        this.interfaces = interfaces;

        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        DialogPane dialogPane = getDialogPane();

        FXMLLoader fxmlLoader = Config.createFXMLLoader("fxml/option_dialog.fxml");
        Parent contentRoot = fxmlLoader.<Parent>load();

        dialogPane.setContent(contentRoot);

        TableView tableView = (TableView) contentRoot.lookup("#tableView");

        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);

        setResultConverter(new Callback<ButtonType, T>() {
            @Override
            public T call(ButtonType param) {
                return null;
            }
        });
    }
}
