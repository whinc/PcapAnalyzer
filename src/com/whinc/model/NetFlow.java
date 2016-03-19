package com.whinc.model;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络流
 * Created by Administrator on 2016/3/18.
 */
public class NetFlow {
    private final List<PacketInfo> packetInfos;

    // 数据包按下面5个属性来划分到网络流
    int srcIp;
    int dstIp;
    int srcPort;
    int dstPort;
    boolean reversed;       // 逆向相等，
                            // 即数据包的源ip和源端口与网络流的目的ip和目的端口相同，
                            // 即数据包的目的ip和目的端口与网络流的源ip和目的端口相同

    public NetFlow(int srcIp, int dstIp, int srcPort, int dstPort) {
        int ip_1 = (srcIp & 0xFF000000) >>> 24;   // IP地址第一个字节
        int ip_2 = (srcIp & 0x00FF0000) >>> 16;    // IP地址第二个字节
        if (ip_1 == 192 && ip_2 == 168) {       // 如果是本地IP地址则视为本机地址
            this.srcIp = srcIp;
            this.dstIp = dstIp;
            this.srcPort = srcPort;
            this.dstPort = dstPort;
            this.reversed = false;
        } else {
            this.srcIp = dstIp;
            this.dstIp = srcIp;
            this.srcPort = dstPort;
            this.dstPort = srcPort;
            this.reversed = true;
        }
        packetInfos = new ArrayList<>();
    }

    /**
     * 判断数据包是否属于当前网络流
     * @param packetInfo
     * @return
     */
    public boolean contain(PacketInfo packetInfo) {
        boolean b = false;

        PcapPacket packet = packetInfo.getPacket();
        Tcp tcp = new Tcp();
        Ip4 ip4 = new Ip4();
        if (packet.hasHeader(ip4)
                && packet.hasHeader(tcp) ) {
            if (ip4.sourceToInt() == srcIp
                    && ip4.destinationToInt() == dstIp
                    && tcp.source() == srcPort
                    && tcp.destination() == dstPort) {
                reversed = false;
                b = true;
            } else if (ip4.sourceToInt() == dstIp
                    && ip4.destinationToInt() == srcIp
                    && tcp.source() == dstPort
                    && tcp.destination() == srcPort) {
                reversed = true;
                b = true;
            }
        }
        return b;
    }

    /**
     * 添加数据包到当前网络流
     * @param packetInfo
     */
    public void add(PacketInfo packetInfo) {
        packetInfos.add(packetInfo);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d:%d %s %d.%d.%d.%d:%d, size:%d",
                (srcIp & 0xFF000000) >>> 24,
                (srcIp & 0x00FF0000) >>> 16,
                (srcIp & 0x0000FF00) >>> 8,
                (srcIp & 0x000000FF),
                srcPort,
                reversed ? "<-" : "->",
                (dstIp & 0xFF000000) >>> 24,
                (dstIp & 0x00FF0000) >>> 16,
                (dstIp & 0x0000FF00) >>> 8,
                (dstIp & 0x000000FF),
                dstPort,
                packetInfos.size()
                );
    }
}
