package com.whinc.util;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.lan.*;
import org.jnetpcap.protocol.network.*;
import org.jnetpcap.protocol.sigtran.Sctp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.voip.Rtp;
import org.jnetpcap.protocol.voip.Sip;
import org.jnetpcap.protocol.vpn.L2TP;
import org.jnetpcap.protocol.wan.PPP;

/**
 * Created by Administrator on 2016/3/6.
 */
public class PacketUtils {

    private static String name(Class<? extends JHeader> head) {
        return head.getSimpleName().toUpperCase();
    }

    public static String parseProtocolName(PcapPacket packet) {
        String name = "Unknown";

        /* 数据链路层协议 */
        if (packet.hasHeader(Ethernet.ID)) {
            name = name(Ethernet.class);
        }
        if (packet.hasHeader(IEEESnap.ID)) {
            name = name(IEEESnap.class);
        }
        if (packet.hasHeader(PPP.ID)) {
            name = name(PPP.class);
        }
        if (packet.hasHeader(IEEE802dot1q.ID)) {
            name = name(IEEE802dot1q.class);
        }
        if (packet.hasHeader(IEEE802dot2.ID)) {
            name = name(IEEE802dot2.class);
        }
        if (packet.hasHeader(IEEE802dot3.ID)) {
            name = name(IEEE802dot3.class);
        }
        if (packet.hasHeader(SLL.ID)) {
            name = name(SLL.class);
        }

        /* 网络层协议 */
        if (packet.hasHeader(Ip4.ID)) {
            name = name(Ip4.class);
        }
        if (packet.hasHeader(Ip6.ID)) {
            name = name(Ip6.class);
        }
        if (packet.hasHeader(Icmp.ID)) {
            name = name(Icmp.class);
        }

        /* 传输层协议 */
        if (packet.hasHeader(Tcp.ID)) {
            name = name(Tcp.class);
        }
        if (packet.hasHeader(Udp.ID)) {
            name = name(Udp.class);
        }
        if (packet.hasHeader(Sctp.ID)) {
            name = name(Sctp.class);
        }

        /* 会话层 */
        if (packet.hasHeader(L2TP.ID)) {
            name = name(L2TP.class);
        }
        if (packet.hasHeader(Rtp.ID)) {
            name = name(Rtp.class);
        }

        /* 应用层协议 */
        if (packet.hasHeader(Http.ID)) {
            name = name(Http.class);
        }
        if (packet.hasHeader(Sip.ID)) {
            name = name(Sip.class);
        }
        return name;
    }

    /**
     * 视作无符号整型
     * @param b
     * @return
     */
    public static int asUByte(byte b) {
        if (b >= 0) {
            return b;
        } else {
            return (b & 0x7F) + 128;
        }
    }

    public static String getReadableIp(byte[] addr) {
        return String.format("%d.%d.%d.%d",
                asUByte(addr[0]),
                asUByte(addr[1]),
                asUByte(addr[2]),
                asUByte(addr[3])
        );
    }
}
