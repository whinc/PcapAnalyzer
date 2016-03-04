package com.whinc.pcap;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.PcapInteger;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/4.
 */
public class PcapManager {
    private static final PcapManager pcapManager = new PcapManager();

    public PcapIf getCurrentPcapIf() {
        return currentPcapIf;
    }

    public void setCurrentPcapIf(PcapIf currentPcapIf) {
        this.currentPcapIf = currentPcapIf;
    }

    private PcapIf currentPcapIf;

    private PcapManager() {}

    public static PcapManager getInstance() {
        return pcapManager;
    }

    public List<PcapIf> getDeviceList() {
        List<PcapIf> pcapIfs = new ArrayList<>();
        StringBuilder errBuf = new StringBuilder();
        int code = Pcap.findAllDevs(pcapIfs, errBuf);
        if (code != Pcap.OK || pcapIfs.isEmpty()) {
            System.err.println("There is no network interface:" + errBuf);
        }
        return pcapIfs;
    }

    public static void capture() {

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
        int index = 3;
//        int index = 0;
//        try {
//            index = new Scanner(System.in).nextInt();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

        PcapBpfProgram program = new PcapBpfProgram();
        String filterExp = "tcp port 80";
        int optimise = 1;   // 1 表示优化，其他值表示不优化
        int netmask = 0xFFFFFF00;
        if (pcap.compile(program, filterExp, optimise, netmask) != Pcap.OK) {
            System.out.println("compile filter expression failed:" + pcap.getErr());
            return;
        }
        if (pcap.setFilter(program) != Pcap.OK) {
            System.out.println("setup filter program failed:" + pcap.getErr());
            return;
        }

        PcapPacketHandler packetHandler = new PcapPacketHandler() {

            @Override
            public void nextPacket(PcapPacket pcapPacket, Object o) {
                Ethernet ethernet = new Ethernet();
                Ip4 ip4 = new Ip4();
                Tcp tcp = new Tcp();
                Udp udp = new Udp();
                Http http = new Http();
//                if (pcapPacket.hasHeader(ethernet)) {
//                    System.out.println(ethernet.toString());
//                }
//                if (pcapPacket.hasHeader(ip4)) {
//                    System.out.println(ip4);
//                }
//                if (pcapPacket.hasHeader(tcp)) {
//                    System.out.println(tcp);
//                }
//                if (pcapPacket.hasHeader(udp)) {
//                    System.out.println(udp);
//                }
                if (pcapPacket.hasHeader(http)) {
                    System.out.println(http);
                }
            }
        };

        pcap.loop(10, packetHandler, null);

        PcapPacket packet = new PcapPacket(JMemory.Type.POINTER);
        int count = 0;
        while (pcap.nextEx(packet) == Pcap.NEXT_EX_OK) {
            System.out.println("count:" + (++count));
            Http http = new Http();
            if (packet.hasHeader(http)) {
                System.out.println(http);
            }
        }
        pcap.close();
    }
}
