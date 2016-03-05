package com.whinc.controller;

import com.whinc.Config;
import com.whinc.model.NetworkAdapter;
import com.whinc.pcap.PcapIfWrapper;
import com.whinc.pcap.PcapManager;
import com.whinc.ui.OptionDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jnetpcap.PcapIf;

import java.io.File;
import java.util.*;

/**
 * Created by Administrator on 2016/3/2.
 */
public class MainFormController {
    @FXML  public Label statusInfoLabel;
    private Stage stage;
    @FXML public MenuItem menuItemStop;
    @FXML private TableView tableView;
    @FXML private MenuItem menuItemStart;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    protected void handlerButtonClick() {
        Dialog<Object> dialog = new Dialog<>();
        dialog.setContentText("hello");
        dialog.showAndWait();
    }

    @FXML
    protected void startCapture() {
        System.out.println("start capture");

        PcapManager.capture();
    }

    /**
     * Open file and load offline *.pcap data
     * @param event
     */
    @FXML protected void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Config.getString("label_open_file"));
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("libpcap", "*.pcap")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null && file.exists()) {
            System.out.println(file);
        }
    }

    @FXML
    protected void switchLanguage(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        if (source != null) {
            switch (source.getId()) {
                case "lang_en":
                    break;
                case "lang_zh":
                    break;
            }
        }
    }

    @FXML protected void startCapture(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        PcapIf pcapIf = PcapManager.getInstance().getNetworkAdapter().getPcapIf();
        if (pcapIf == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(Config.getString("label_message"));
            String text = Config.getString("label_info_select_net_if");
            alert.setHeaderText(text);
            alert.showAndWait();

            setStatusWarning(text);
            return;
        }

        setStatusInfo(Config.getString("label_start_capture"));
        source.setDisable(true);
        menuItemStop.setDisable(false);
    }

    @FXML protected void stopCapture(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();

        setStatusInfo(Config.getString("label_stop_capture"));
        source.setDisable(true);
        menuItemStart.setDisable(false);
    }

    @FXML protected void showCaptureOptions(ActionEvent event) {

        List<PcapIf> interfaces = PcapManager.getInstance().getDeviceList();
        OptionDialog optionDialog = new OptionDialog(interfaces);
        Optional<NetworkAdapter> optional = optionDialog.showAndWait();
        if (optional.isPresent()) {
            NetworkAdapter networkAdapter = optional.get();
            PcapManager.getInstance().setNetworkAdapter(networkAdapter);

            setStatusInfo(String.format(Config.getString("label_select_xx"), networkAdapter));
        } else {
            setStatusWarning(Config.getString("label_select_nothing"));
        }

//        ChoiceDialog<PcapIfWrapper> choiceDialog = new ChoiceDialog<>();
//
//        // setup dialog
//        choiceDialog.setTitle(Config.getString("label_choose_network_interface"));
//        choiceDialog.setHeaderText(Config.getString("label_choose_network_interface_description"));
//        ImageView graphic = new ImageView(new Image(Config.getResource("images/ic_network.png").toString()));
//        graphic.setFitWidth(60.0);
//        graphic.setFitHeight(60.0);
//        graphic.setSmooth(true);
//        choiceDialog.setGraphic(graphic);
//
//        // set data
//        List<PcapIf> deviceList = PcapManager.getInstance().getDeviceList();
//        List<PcapIfWrapper> pcapIfs = new ArrayList<>(deviceList.size());
//        for (int i = 0; i < deviceList.size(); ++i) {
//            pcapIfs.add(new PcapIfWrapper(i+1, deviceList.get(i)));
//        }
//        choiceDialog.getItems().addAll(pcapIfs);
//
//        // get result
//        Optional<PcapIfWrapper> optional = choiceDialog.showAndWait();
//        if (optional.isPresent()) {
//            PcapIf pcapIf = optional.get().getPcapIf();
//            PcapManager.getInstance().setCurrentPcapIf(pcapIf);
//
//            setStatusInfo(String.format(Config.getString("label_select_xx"), optional.get()));
//        } else {
//            setStatusWarning(Config.getString("label_select_nothing"));
//        }
    }

    private void setStatusError(String text) {
        setStatusText(text, Paint.valueOf("#F44336"));
    }

    private void setStatusInfo(String text) {
        setStatusText(text, Paint.valueOf("black"));
    }

    private void setStatusWarning(String text) {
        setStatusText(text, Paint.valueOf("#3F61BF"));
    }

    private void setStatusText(String text, Paint paint) {
        statusInfoLabel.setText(text);
        statusInfoLabel.setTextFill(paint);
    }
}
