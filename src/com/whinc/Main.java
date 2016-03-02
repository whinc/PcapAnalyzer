package com.whinc;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;

/**
 * Created by whinc on 3/2/16.
 */
public class Main {
    public static void main(String[] args) {
        ArrayList<PcapIf> pcapIfs = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        Pcap.findAllDevs(pcapIfs, stringBuilder);
        for (PcapIf v : pcapIfs) {
            System.out.println("name:" + v.getName());
        }
    }
}
