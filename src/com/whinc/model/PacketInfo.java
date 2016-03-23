package com.whinc.model;

import com.whinc.Config;
import com.whinc.util.PacketUtils;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;

/**
 * {@link PacketInfo}获取数据包的相关信息，内部只保存{@link PcapPacket}的引用
 */
public class PacketInfo {

    /** 标识数据包的流向。true表示数据包发出，false表示数据包流入*/
    boolean reversed;
    private PcapPacket packet;
    private String name;

    public PacketInfo(PcapPacket packet) {
        this.packet = packet;
    }

    public PcapPacket getPacket() {
        return packet;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
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
        if (name == null || name.isEmpty()) {
            name = PacketUtils.parseProtocolName(packet);
        }
        return name;
    }

    public long getLength() {
        return packet.getTotalSize();
    }

    public String getInfo() {
        return "";
    }
}
