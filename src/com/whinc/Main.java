package com.whinc;

import com.whinc.ui.MainForm;
import javafx.application.Application;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by whinc on 3/2/16.
 */
public class Main {
    public static void main(String[] args) {
//        Application.launch(MainForm.class, args);

        ArrayList<PcapIf> pcapIfs = new ArrayList<>();
        StringBuilder errBuf = new StringBuilder();
        int code = Pcap.findAllDevs(pcapIfs, errBuf);
        if (code != Pcap.OK || pcapIfs.isEmpty()) {
            System.err.println("There is no network interface:" + errBuf);
            return;
        }
        for (int i = 0; i < pcapIfs.size(); ++i) {
            PcapIf pcapIf = pcapIfs.get(i);
            System.out.println(String.format("%d %s %s", i, pcapIf.getName(), pcapIf.getDescription()));
        }
        System.out.println("Enter interface index which will be sniffed:");
        int index = 0;
        try {
            index = new Scanner(System.in).nextInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PcapIf pcapIf = pcapIfs.get(index);
        Pcap pcap = Pcap.openLive(pcapIf.getName(),
                Pcap.DEFAULT_SNAPLEN,
                Pcap.DEFAULT_PROMISC,
                Pcap.DEFAULT_TIMEOUT,
                errBuf);
        if (pcap == null) {
            System.err.println("Error while open device interface:" + errBuf);
            return;
        }

        PcapPacketHandler packetHandler = new PcapPacketHandler() {

            @Override
            public void nextPacket(PcapPacket pcapPacket, Object o) {
                System.out.println(pcapPacket.toString());
            }
        };

        pcap.loop(10, packetHandler, null);

        pcap.close();
    }
}
