package com.whinc.ui;

import com.whinc.Config;
import com.whinc.model.NetworkAdapter;
import com.whinc.pcap.PcapManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import org.jnetpcap.PcapIf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/4.
 */
public class OptionDialog extends Dialog<NetworkAdapter>
        implements ChangeListener<String>{

    private List<PcapIf> interfaces;    // network card interface
    private List<NetworkAdapter> networkAdapters;
    private TextField filterExpTxt;
    private Button compileBtn;

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
        dialogPane.setContent(contentRoot);     // 设置对话框布局

        setTitle(Config.getString("title_option_dialog"));
        ButtonType start = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(start, ButtonType.CLOSE);

        // libpcap 过滤表达式
        filterExpTxt = (TextField) contentRoot.lookup("#filterExpTxt");
        filterExpTxt.textProperty().addListener(this);

        compileBtn = (Button) contentRoot.lookup("#compileBtn");
        compileBtn.setOnAction(event -> {
            String text = filterExpTxt.getText();
            PcapManager pcapManager = PcapManager.getInstance();
            NetworkAdapter networkAdapter = getSelectAdapter();
            boolean b = pcapManager.compile(networkAdapter.getName(), text);
            System.out.println("compile:" + b);
        });

        initTableView();
        setResultConverter(param -> {
            // 获取选中的网络适配器，并保存到全局变量中
            PcapManager pcapManager = PcapManager.getInstance();
            pcapManager.setNetworkAdapter(getSelectAdapter());
            pcapManager.setFilterExp(filterExpTxt.getText());
            if( param.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return pcapManager.getNetworkAdapter();
            } else {
                return null;
            }
        });
    }

    private NetworkAdapter getSelectAdapter() {
        NetworkAdapter adapter = null;
        for (NetworkAdapter e : networkAdapters) {
            if (e.isChecked()) {
                adapter = e;
                break;
            }
        }
        return adapter;
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
            // 为表格每行注册监听器，点击表格某行时切换到该网络适配器
            adapter.checked.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    for (NetworkAdapter v : networkAdapters) {
                        if (v != adapter) {
                            v.checked.setValue(false);
                            disableFilter(false);
                        }
                    }
                }
            });
            networkAdapters.add(adapter);
        }
        tableView.getItems().addAll(networkAdapters);

        // 如果之前已经选择过网络适配器，则恢复之前的状态
        NetworkAdapter curAdapter = PcapManager.getInstance().getNetworkAdapter();
        if (curAdapter != null) {
            for (NetworkAdapter v : networkAdapters) {
                if (v.getName().equals(curAdapter.getName())) {
                    v.setChecked(true);
                    String filterExp = PcapManager.getInstance().getFilterExp();
                    filterExpTxt.setText(filterExp);
                    disableFilter(false);
                    break;
                }
            }
        }
    }

    private void disableFilter(boolean b) {
        filterExpTxt.setDisable(b);
        compileBtn.setDisable(b);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        Background background;
        String filterExp = newValue;
        if (filterExp == null || filterExp.isEmpty()) {
            background = new Background(new BackgroundFill(Paint.valueOf("#FFF"), new CornerRadii(0), new Insets(0)));
            filterExpTxt.setBackground(background);
            return;
        }
        PcapManager pcapManager = PcapManager.getInstance();
        String adapterName = getSelectAdapter().getName();
        boolean result = pcapManager.compile(adapterName, filterExp);
        System.out.println("filter expression compile:" + result);
        if (result) {
            background = new Background(new BackgroundFill(Paint.valueOf("rgb(175,255,175)"), new CornerRadii(0), new Insets(0)));
        } else {
            background = new Background(new BackgroundFill(Paint.valueOf("rgb(255,175,175)"), new CornerRadii(0), new Insets(0)));
        }
        filterExpTxt.setBackground(background);
    }
}
