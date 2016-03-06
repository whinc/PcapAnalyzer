package com.whinc.model;

import com.whinc.util.PacketUtils;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;

/**
 * Created by Administrator on 2016/3/5.
 */
public class PacketInfo {
    private static long firstTimestamp = 0;
    private PcapPacket packet;

    public PacketInfo(PcapPacket packet) {
        this.packet = new PcapPacket(packet);
    }

    public long getNumber() {
        return packet.getFrameNumber();
    }

    public long getTimestamp() {
        if (firstTimestamp == 0) {
            firstTimestamp = packet.getCaptureHeader().timestampInMicros();
        }
        return packet.getCaptureHeader().timestampInMicros() - firstTimestamp;
    }

    public String getSourcee() {
        String addr = "unknown";
        Ip4 ip4 = new Ip4();
        if (packet.hasHeader(ip4)) {
            addr = PacketUtils.getReadableIp(ip4.source());
        }
        return addr;
    }

    public String getDestination() {
        String addr = "unknown";
        Ip4 ip4 = new Ip4();
        if (packet.hasHeader(ip4)) {
            addr = PacketUtils.getReadableIp(ip4.destination());
        }
        return addr;
    }

    public String getProtocolName() {
        return PacketUtils.parseProtocolName(packet);
    }

    public long getLength() {
        return packet.getTotalSize();
    }

    public String getInfo() {
        return "";
    }
}
