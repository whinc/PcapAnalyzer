package com.whinc.model;

import javafx.beans.property.SimpleBooleanProperty;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 * Created by Administrator on 2016/3/5.
 */
public class NetworkAdapter {
    public SimpleBooleanProperty checked;

    public PcapIf getPcapIf() {
        return pcapIf;
    }

    private PcapIf pcapIf;
    private int number;

    public NetworkAdapter(int number, PcapIf pcapIf) {
        this.checked = new SimpleBooleanProperty(false);
        this.number = number;
        this.pcapIf = pcapIf;
    }

    public int getNumber() {
        return number;
    }

    public boolean isChecked() {
        return checked.get();
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    @Override
    public String toString() {
        return String.format("%d\t%s\t%s",  getNumber(), getName(), getDescription());
    }

    public String getName() {
        return pcapIf.getName();
    }

    public String getDescription() {
        return pcapIf.getDescription();
    }
}
