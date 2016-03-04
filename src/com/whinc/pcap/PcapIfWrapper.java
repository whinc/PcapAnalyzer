package com.whinc.pcap;

import org.jnetpcap.PcapIf;

/**
 * Created by Administrator on 2016/3/4.
 */
public class PcapIfWrapper {
    private int index;

    public PcapIf getPcapIf() {
        return pcapIf;
    }

    private PcapIf pcapIf;

    public PcapIfWrapper(int index, PcapIf pcapIf) {
        this.index = index;
        this.pcapIf = pcapIf;
    }

    @Override
    public String toString() {
        return String.format("%2d\t%s\t\t%s", index, pcapIf.getName(), pcapIf.getDescription());
    }
}
