package com.whinc.ui;

import com.whinc.Config;
import com.whinc.model.NetworkAdapter;
import com.whinc.pcap.PcapManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/4.
 */
public class OptionDialog extends Dialog<NetworkAdapter>{

    private List<PcapIf> interfaces;    // network card interface
    private List<NetworkAdapter> networkAdapters;

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
        FXMLLoader fxmlLoader = Config.createFXMLLoader("fxml/option_dialog.fxml");
        Parent contentRoot = fxmlLoader.<Parent>load();
        contentRoot.getStylesheets().addAll("css/option_dialog.css");

        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(contentRoot);

        setTitle(Config.getString("title_option_dialog"));

        initTableView();

        ButtonType start = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(start, ButtonType.CLOSE);

        setResultConverter(param -> {
            for (NetworkAdapter e : networkAdapters) {
                if (e.isChecked()) {
                    PcapManager.getInstance().setNetworkAdapter(e);
                    break;
                }
            }
            if( param.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return PcapManager.getInstance().getNetworkAdapter();
            } else {
                return null;
            }
        });
    }

    private void initTableView() {

        Node contentRoot = getDialogPane().getContent();
        TableView<NetworkAdapter> tableView = (TableView<NetworkAdapter>) contentRoot.lookup("#tableView");

        // setup table cell factory
        TableColumn<NetworkAdapter, Boolean> columnChoice = (TableColumn<NetworkAdapter, Boolean>) tableView.getColumns().get(0);
        columnChoice.setCellValueFactory(param -> param.getValue().checked);
        columnChoice.setCellFactory(CheckBoxTableCell.forTableColumn(columnChoice));
        TableColumn<NetworkAdapter, ?> columnNumber = tableView.getColumns().get(1);
        columnNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        TableColumn<NetworkAdapter, ?> columnName = tableView.getColumns().get(2);
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<NetworkAdapter, ?> columnDescription = tableView.getColumns().get(3);
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));


        // setup table data
        List<PcapIf> pcapIfs = PcapManager.getInstance().getDeviceList();
        networkAdapters = new ArrayList<>(pcapIfs.size());
        int number = 1;
        for (PcapIf e : pcapIfs) {
            NetworkAdapter adapter = new NetworkAdapter(number++, e);
            // only one row can be checked
            adapter.checked.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    for (NetworkAdapter v : networkAdapters) {
                        if (v != adapter) {
                            v.checked.setValue(false);
                        }
                    }
                }
            });
            networkAdapters.add(adapter);
        }
        tableView.getItems().addAll(networkAdapters);

        // restore last selection
        NetworkAdapter curAdapter = PcapManager.getInstance().getNetworkAdapter();
        if (curAdapter != null) {
            for (NetworkAdapter v : networkAdapters) {
                if (v.getName().equals(curAdapter.getName())) {
                    v.setChecked(true);
                    break;
                }
            }
        }
    }
}
