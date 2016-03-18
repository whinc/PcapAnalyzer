package com.whinc.controller;

import com.whinc.Config;
import com.whinc.model.NetworkAdapter;
import com.whinc.model.PacketInfo;
import com.whinc.pcap.ClusterModule;
import com.whinc.pcap.PcapManager;
import com.whinc.ui.OptionDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Created by Administrator on 2016/3/2.
 */
public class MainFormController {
    private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
    @FXML  public Label statusInfoLabel;
    public TextArea packetDetailText;
    @FXML public TextArea logText;
    @FXML public Tab logTab;
    @FXML public TabPane tabPane;
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


    /**
     * 初始化 （该方法在FXMLLoader加载布局文件时自动调用）
     */
    @FXML protected void initialize() {
        System.out.println("Begin initialize");

        ObservableList<TableColumn<PacketInfo, String>> columns = tableView.getColumns();
        TableColumn<PacketInfo, String> numCol = columns.get(0);
        numCol.setCellValueFactory(param -> {
            PacketInfo packetInfo = param.getValue();
            return new SimpleStringProperty(String.valueOf(packetInfo.getNumber()));
        });
        TableColumn<PacketInfo, String> timeCol = columns.get(1);
        timeCol.setCellValueFactory(param -> {
            PacketInfo packetInfo = param.getValue();
            return new SimpleStringProperty(String.format("%.6f", packetInfo.getTimestamp() / 1e6));
        });
        TableColumn<PacketInfo, String> srcCol = columns.get(2);
        srcCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getSourcee());
        });
        TableColumn<PacketInfo, String> dstCol = columns.get(3);
        dstCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getDestination());
        });
        TableColumn<PacketInfo, String> protocolCol = columns.get(4);
        protocolCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getProtocolName());
        });
        TableColumn<PacketInfo, String> lengthCol = columns.get(5);
        lengthCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getLength()));
        });
        TableColumn<PacketInfo, String> infoCol = columns.get(6);
        infoCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getInfo());
        });

        tableView.setOnMouseClicked(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            ObservableList<PacketInfo> items = tableView.getItems();
            if (selectedIndex >= 0 && selectedIndex < items.size()) {
                packetDetailText.setText(items.get(selectedIndex).getPacket().toString());
            }
        });

        System.out.println("End initialize");
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
        if (file == null || !file.exists()) {
            System.err.println("Can't find file:" + file);
            return;
        }

        ObservableList data = tableView.getItems();
        data.clear();
        Config.setTimestamp(Config.DEFAULT_TIMESTAMP);

        // 捕获离线数据包
        PcapManager.getInstance().captureOffline(file, packet -> {
            // 将第一个数据包的时间戳设置为起始时间
            if (Config.getTimestamp() <= Config.DEFAULT_TIMESTAMP) {
                Config.setTimestamp(packet.getCaptureHeader().timestampInMicros());
            }

            PcapPacket packetCopy = new PcapPacket(packet); // 获取副本
            data.add(new PacketInfo(packetCopy));
        });
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
        NetworkAdapter networkAdapter = PcapManager.getInstance().getNetworkAdapter();
        if (networkAdapter == null) {
            showCaptureOptions(null);
            return;
        }

        setStatusInfo(Config.getString("label_start_capture"));
        source.setDisable(true);
        menuItemStop.setDisable(false);

        PcapManager.getInstance().captureLive(packet -> {
            // 将第一个数据包的时间戳设置为起始时间
            if (Config.getTimestamp() <= Config.DEFAULT_TIMESTAMP) {
                Config.setTimestamp(packet.getCaptureHeader().timestampInMicros());
            }

            PcapPacket packetCopy = new PcapPacket(packet); // 获取副本
            ObservableList items = tableView.getItems();
            items.add(new PacketInfo(packetCopy));
        });
    }

    @FXML protected void stopCapture(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();

        setStatusInfo(Config.getString("label_stop_capture"));
        source.setDisable(true);
        menuItemStart.setDisable(false);

        PcapManager.getInstance().stopCapture();

        setStatusInfo(Config.getString("label_pcap_ready"));
    }

    @FXML protected void showCaptureOptions(ActionEvent event) {

        List<PcapIf> interfaces = PcapManager.getInstance().getDeviceList();
        OptionDialog optionDialog = new OptionDialog(interfaces);
        Optional<NetworkAdapter> optional = optionDialog.showAndWait();
        if (optional.isPresent()) {     // If result is not null, start capture immediately
            startCapture(new ActionEvent(menuItemStart, null));
        } else {
            NetworkAdapter networkAdapter = PcapManager.getInstance().getNetworkAdapter();
            if (networkAdapter != null) {
                setStatusInfo(String.format(Config.getString("label_select_xx"), networkAdapter));
            } else {
                setStatusWarning(Config.getString("label_select_nothing"));
            }
        }
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

    @FXML protected void exit(ActionEvent event) {
        if (stage != null) {
            stage.close();
        }
    }

    @FXML public void clear(ActionEvent event) {
        tableView.getItems().clear();
    }

    @FXML public void showAboutDialog(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.NONE,
                "Author: whinc\n\nE-mail: xiaohui_hubei@163.com\n",
                ButtonType.OK);
        alert.setTitle("About");
        alert.showAndWait();
    }

    @FXML public void extractVector(ActionEvent event) {
        // 先停止捕获
        stopCapture(new ActionEvent(menuItemStop, null));

        // 提取网络流行为特征
        ClusterModule.getInstance().extractVector(tableView.getItems());

        appendLog(ClusterModule.getInstance().toString());
        tabPane.getSelectionModel().select(logTab);
    }

    private void appendLog(String log) {
        String datetime = LOG_DATE_FORMAT.format(Calendar.getInstance().getTime());

        StringBuilder builder = new StringBuilder(logText.getText());
        builder.append(datetime)
                .append("\n")
                .append(log);
        logText.setText(builder.toString());
    }
}
