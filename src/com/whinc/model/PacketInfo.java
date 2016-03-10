package com.whinc.model;

import com.whinc.Config;
import com.whinc.util.PacketUtils;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;

/**
 * Created by Administrator on 2016/3/5.
 */
public class PacketInfo {

    public PcapPacket getPacket() {
        return packet;
    }

    private PcapPacket packet;

    public PacketInfo(PcapPacket packet) {
        this.packet = packet;
    }

    public long getNumber() {
        return packet.getFrameNumber();
    }

    public long getTimestamp() {
        return packet.getCaptureHeader().timestampInMicros() - Config.getTimestamp();
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
