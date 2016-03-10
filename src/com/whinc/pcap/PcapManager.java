package com.whinc.pcap;

import com.whinc.Config;
import com.whinc.model.NetworkAdapter;
import com.whinc.model.PacketInfo;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/3/4.
 */
public class PcapManager {
    private static final PcapManager pcapManager = new PcapManager();
    private Task<Void> task;
    private Pcap pcap;

    public NetworkAdapter getNetworkAdapter() {
        return networkAdapter;
    }

    public void setNetworkAdapter(NetworkAdapter networkAdapter) {
        this.networkAdapter = networkAdapter;
    }

    private NetworkAdapter networkAdapter;

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

    /**
     * 实时捕获网络适配器数据包
     * @param packetInfos
     */
    public boolean startCapture(ObservableList<PacketInfo> packetInfos, ChangeListener changeListener) {
        if (networkAdapter == null || networkAdapter.getPcapIf() == null) {
            System.err.println("Network adapter is null");
            return false;
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
            return false;
        }

        task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                PcapPacketHandler<Void> handler = new PcapPacketHandler<Void>() {

                    @Override
                    public void nextPacket(PcapPacket pcapPacket, Void aVoid) {
                        if (Config.getTimestamp() <= Config.DEFAULT_TIMESTAMP) {
                            Config.setTimestamp(pcapPacket.getCaptureHeader().timestampInMicros());
                        }
                        packetInfos.add(new PacketInfo(pcapPacket));
                        // 通知更新
                        updateProgress(packetInfos.size(), Long.MAX_VALUE);
                    }
                };
                pcap.loop(-1, handler, null);

                return null;
            }

        };
        if (changeListener != null) {
            task.progressProperty().addListener(changeListener);
        }
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        return true;
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
