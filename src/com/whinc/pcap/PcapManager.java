package com.whinc.pcap;

import com.whinc.model.NetworkAdapter;
import javafx.concurrent.Task;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/4.
 */
public class PcapManager {
    private static final PcapManager pcapManager = new PcapManager();
    private Task<Void> task;
    private Pcap pcap;
    private NetworkAdapter networkAdapter;
    private String filterExp;               // libpcap捕获过滤表达式

    private PcapManager() {}

    public static PcapManager getInstance() {
        return pcapManager;
    }

    public static boolean compile(String interfaceName, String filter) {

        StringBuilder errBuf = new StringBuilder();
        Pcap pcap = Pcap.openLive(interfaceName,
                Pcap.DEFAULT_SNAPLEN,
                Pcap.DEFAULT_PROMISC,
                Pcap.DEFAULT_TIMEOUT,
                errBuf);

        if (pcap == null) {
            System.err.println("Error while open device interface:" + errBuf);
            return false;
        }

        PcapBpfProgram program = new PcapBpfProgram();
        int optimize = 0;
        int netmask = 0xFFFFFF00;
        if (pcap.compile(program, filter, optimize, netmask) != Pcap.OK) {
            return false;
        }

        pcap.close();
        return true;
//        int len = 64 * 1024;
//        int datalinkType = Ethernet.EthernetType.IP4.getId();
//        int optimize = 0;
//        int netmask = 0xFFFFFF00;
//        filterProgram = new PcapBpfProgram();
//        if (Pcap.compileNoPcap(len, datalinkType, filterProgram, filter, 0, netmask) != Pcap.OK) {
//            filterProgram = null;       // 如果编译失败将其置为null，避免错误使用
//            System.err.println("error");
//            return false;
//        }
//        return true;
    }

    public NetworkAdapter getNetworkAdapter() {
        return networkAdapter;
    }

    public void setNetworkAdapter(NetworkAdapter networkAdapter) {
        this.networkAdapter = networkAdapter;
    }

    public String getFilterExp() {
        return filterExp;
    }

    public void setFilterExp(String filterExp) {
        this.filterExp = filterExp;
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

    public void captureLive(OnCapturePacketListener listener) {
        if (networkAdapter == null || networkAdapter.getPcapIf() == null) {
            System.err.println("Network adapter is null");
            return ;
        }
        stopCapture();

        PcapIf pcapIf = networkAdapter.getPcapIf();
        StringBuilder errBuf = new StringBuilder();
        pcap = Pcap.openLive(pcapIf.getName(),
                Pcap.DEFAULT_SNAPLEN,
                Pcap.DEFAULT_PROMISC,
                Pcap.DEFAULT_TIMEOUT,
                errBuf);
        if (pcap == null) {
            System.err.println("Error while open device interface:" + errBuf);
            return ;
        }

        if (filterExp != null && !filterExp.isEmpty()) {
            int optimize = 1;
            int netmask = 0xFFFFFF00;
            PcapBpfProgram program = new PcapBpfProgram();
            if (pcap.compile(program, filterExp, optimize, netmask) != Pcap.OK) {
                System.err.println("Invalid capture filter expression!");
            } else {
                pcap.setFilter(program);
            }
        }

        task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                PcapPacketHandler<Void> handler = new PcapPacketHandler<Void>() {

                    @Override
                    public void nextPacket(PcapPacket pcapPacket, Void aVoid) {
//                        if (Config.getTimestamp() <= Config.DEFAULT_TIMESTAMP) {
//                            Config.setTimestamp(pcapPacket.getCaptureHeader().timestampInMicros());
//                        }
//                        packetInfos.add(new PacketInfo(pcapPacket));
//                        // 通知更新
//                        updateProgress(packetInfos.size(), Long.MAX_VALUE);
                        if (listener != null) {
                            listener.onCapture(pcapPacket);
                        }
                    }
                };
                pcap.loop(-1, handler, null);

                return null;
            }

        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 停止实时捕获
     */
    public void stopCapture() {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        if (pcap != null) {
            try {
                pcap.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 捕获离线网络数据包
     * @param file
     * @param listener
     */
    public void captureOffline(File file, OnCapturePacketListener listener) {
        if (file == null || !file.exists() || !file.isFile()) {
            System.err.println("Can not open file:" + file);
            return;
        }

        stopCapture();

        StringBuilder errBuf = new StringBuilder();
        Pcap pcap = Pcap.openOffline(file.getAbsolutePath(), errBuf);
        if (pcap == null) {
            System.err.println("Error while open device interface:" + errBuf);
            return;
        }

        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                PcapPacket packet = new PcapPacket(JMemory.Type.POINTER);
                while (pcap.nextEx(packet) == Pcap.NEXT_EX_OK) {
                    if (listener != null) {
                        listener.onCapture(packet);
                    }
                }
                pcap.close();
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 数据包捕获监听
     */
    public interface OnCapturePacketListener {
        /**
         * 每当捕获到一个数据包时被调用
         * @param packet 被捕获的数据包，该数据包指向一个临时存储区，不要保存这个引用，
         *               如果需要保存数据包使用{@code new PcapPacket(packet);}创建一个新的副本
         */
        void onCapture(PcapPacket packet);
    }
}
