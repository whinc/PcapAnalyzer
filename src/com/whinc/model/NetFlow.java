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

    /* 数据包按下面5个属性来划分到网络流 */
    /** 源IP地址 */
    int srcIp;
    /** 目的IP地址 */
    int dstIp;
    /** 源端口 */
    int srcPort;
    /** 目的端口 */
    int dstPort;

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /** 网络流是否已结束 */
    boolean closed = false;

    /* 网络流行为特征 */
    long inTotalPacketNum;
    /** 流入数据包的总字节大小 */
    long inTotalPacketVolume;
    /** 流入数据包的平均字节大小 */
    float inAveragePacketLen;
    /** 流入数据包的大小的均方差 */
    float inPacketLenStdDeviation;
    /** 流入数据包的最大间隔时间 */
    float inPacketMaxIntervalTime;
    /** 流入数据包的平均间隔时间 */
    float inPacketAverageIntervalTime;
    /** 流入数据包的最小间隔时间 */
    float inPacketMinIntervalTime;
    long outTotalPacketNum;
    /** 流出数据包的总字节大小 */
    long outTotalPacketVolume;
    /** 流出数据包的平均字节大小 */
    float outAveragePacketLen;
    /** 流出数据包的大小的均方差 */
    float outPacketLenStdDeviation;
    /** 流出数据包的最大间隔时间 */
    float outPacketMaxIntervalTime;
    /** 流出数据包的平均间隔时间 */
    float outPacketAverageIntervalTime;
    /** 流出数据包的最小间隔时间 */
    float outPacketMinIntervalTime;
    /** 流入流出数据流量比值 */
    float inOutPacketVolumeRatio;
    /** 流入流出数据包数量比 */
    float inOutPacketNumRatio;
    /** 用于标识当前网络流行为特征数据是否有效，如果增减数据包会导致数据失效，需要重新计算 */
    private boolean invalid = true;

    public NetFlow(int srcIp, int dstIp, int srcPort, int dstPort) {
        int ip_1 = (srcIp & 0xFF000000) >>> 24;   // IP地址第一个字节
        int ip_2 = (srcIp & 0x00FF0000) >>> 16;    // IP地址第二个字节
        if (ip_1 == 192 && ip_2 == 168) {       // 如果是本地IP地址则视为源主机地址
            this.srcIp = srcIp;
            this.dstIp = dstIp;
            this.srcPort = srcPort;
            this.dstPort = dstPort;
        } else {
            this.srcIp = dstIp;
            this.dstIp = srcIp;
            this.srcPort = dstPort;
            this.dstPort = srcPort;
        }
        packetInfos = new ArrayList<>();
    }

    /** 检查数据有效性，如果无效则更新数据 */
    private void check() {
        if (invalid) {
            update();
        }
    }

    private void update() {
        // 重置
        inTotalPacketNum = 0;
        inTotalPacketVolume = 0;
        inAveragePacketLen = 0;
        inPacketLenStdDeviation = 0.0f;
        inPacketMaxIntervalTime = 0.0f;
        inPacketAverageIntervalTime = 0.0f;
        inPacketMinIntervalTime = 0.0f;

        outTotalPacketNum = 0;
        outTotalPacketVolume = 0;
        outAveragePacketLen = 0;
        outPacketLenStdDeviation = 0.0f;
        outPacketMaxIntervalTime = 0.0f;
        outPacketAverageIntervalTime = 0.0f;
        outPacketMinIntervalTime = 0.0f;

        inOutPacketVolumeRatio = 0.0f;
        inOutPacketNumRatio = 0.0f;

        // 更新数据
        long lastInPktTimestamp = 0L;
        long totalInPktIntervalTime = 0L;
        long lastOutPktTimestamp = 0L;
        long totalOutPktIntervalTime = 0L;
        for (PacketInfo pkt : packetInfos) {
            if (pkt.isReversed()) {     // 流入数据包
                inTotalPacketNum += 1;
                inTotalPacketVolume += pkt.getLength();

                // 时间戳
                if (lastInPktTimestamp == 0) {
                    lastInPktTimestamp = pkt.getTimestamp();
                } else {
                    long inInterval = pkt.getTimestamp() - lastInPktTimestamp;
                    if (inInterval > inPacketMaxIntervalTime) {
                        inPacketMaxIntervalTime = inInterval;
                    } else if (inInterval < inPacketMinIntervalTime) {
                        inPacketMinIntervalTime = inInterval;
                    }
                    totalInPktIntervalTime += inInterval;
                }

            } else {                    // 流出数据包
                outTotalPacketNum += 1;
                outTotalPacketVolume += pkt.getLength();

                // 时间戳
                if (lastOutPktTimestamp == 0) {
                    lastOutPktTimestamp = pkt.getTimestamp();
                } else {
                    long outInterval = pkt.getTimestamp() - lastOutPktTimestamp;
                    if (outInterval > outPacketMaxIntervalTime) {
                        outPacketMaxIntervalTime = outInterval;
                    } else if (outInterval < outPacketMinIntervalTime) {
                        outPacketMinIntervalTime = outInterval;
                    }
                    totalOutPktIntervalTime += outInterval;
                }
            }
        }
        // 计算数据包达到的平均间隔时间
        if (inTotalPacketNum != 0) {
            inPacketAverageIntervalTime = totalInPktIntervalTime / inTotalPacketNum;
        }
        if (outTotalPacketNum != 0) {
            outPacketAverageIntervalTime = totalOutPktIntervalTime / outTotalPacketNum;
        }

        // 计算数据包平均大小
        if (inTotalPacketNum != 0) {
            inAveragePacketLen = (float) inTotalPacketVolume / inTotalPacketNum;
        }
        if (outTotalPacketNum != 0) {
            outAveragePacketLen = (float) outTotalPacketVolume / outTotalPacketNum;
        }

        // 计算网络流中数据包大小的均方差
        for (PacketInfo pkt : packetInfos) {
            if (pkt.isReversed()) {     // 流入数据包
                float v = pkt.getLength() - inAveragePacketLen;
                inPacketLenStdDeviation += (v * v);
            } else {                    // 流出数据包
                float v = pkt.getLength() - outAveragePacketLen;
                outPacketLenStdDeviation += (v * v);
            }
        }
        if (inTotalPacketNum != 0) {
            inPacketLenStdDeviation /= inTotalPacketNum;
        }
        if (outTotalPacketNum != 0) {
            outPacketLenStdDeviation /= outTotalPacketNum;
        }

        if (outTotalPacketNum != 0) {
            inOutPacketNumRatio = (float) inTotalPacketNum / outTotalPacketNum;
        }
        inOutPacketVolumeRatio = (float) inTotalPacketVolume / outTotalPacketVolume;

        invalid = false;        // 标记数据为有效状态
    }

    /** 流入数据包的总数 */
    public long getInTotalPacketNum() {
        check();
        return inTotalPacketNum;
    }

    public long getInTotalPacketVolume() {
        check();
        return inTotalPacketVolume;
    }

    public float getInAveragePacketLen() {
        check();
        return inAveragePacketLen;
    }

    public float getInPacketLenStdDeviation() {
        check();
        return inPacketLenStdDeviation;
    }

    public float getInPacketMaxIntervalTime() {
        check();
        return inPacketMaxIntervalTime;
    }

    public float getInPacketAverageIntervalTime() {
        check();
        return inPacketAverageIntervalTime;
    }

    public float getInPacketMinIntervalTime() {
        check();
        return inPacketMinIntervalTime;
    }

    /** 流出数据包的总数 */
    public long getOutTotalPacketNum() {
        check();
        return outTotalPacketNum;
    }

    public long getOutTotalPacketVolume() {
        check();
        return outTotalPacketVolume;
    }

    public float getOutAveragePacketLen() {
        check();
        return outAveragePacketLen;
    }

    public float getOutPacketLenStdDeviation() {
        check();
        return outPacketLenStdDeviation;
    }

    public float getOutPacketMaxIntervalTime() {
        check();
        return outPacketMaxIntervalTime;
    }

    public float getOutPacketAverageIntervalTime() {
        check();
        return outPacketAverageIntervalTime;
    }

    public float getOutPacketMinIntervalTime() {
        check();
        return outPacketMinIntervalTime;
    }

    public float getInOutPacketVolumeRatio() {
        check();
        return inOutPacketVolumeRatio;
    }

    public float getInOutPacketNumRatio() {
        check();
        return inOutPacketNumRatio;
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
                packetInfo.setReversed(false);
                b = true;
            } else if (ip4.sourceToInt() == dstIp
                    && ip4.destinationToInt() == srcIp
                    && tcp.source() == dstPort
                    && tcp.destination() == srcPort) {
                packetInfo.setReversed(true);
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
        invalid = true;     // 标记数据为无效
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d:%d - %d.%d.%d.%d:%d, " +
                "流入:%d, 留出:%d, 总共:%d, \n" +
                "流入数据包长度均方差:%.1f Bytes, 流出数据包长度均方差:%.1f Bytes,\n" +
                "流入数据包平均长度:%.1f Bytes, 流出数据包平均长度:%.1f Bytes,\n" +
                "流入数据包最大间隔时间:%.1f us, 流入数据包最小间隔时间:%.1f us, 流入数据包平均间隔时间:%.1f us,\n" +
                "流出数据包最大间隔时间:%.1f us, 流出数据包最小间隔时间:%.1f us, 流出数据包平均间隔时间:%.1f us,\n" +
                "流入/流出数据包流量比:%.1f, 流入/流出数据包数量比:%.1f\n",
                (srcIp & 0xFF000000) >>> 24,
                (srcIp & 0x00FF0000) >>> 16,
                (srcIp & 0x0000FF00) >>> 8,
                (srcIp & 0x000000FF),
                srcPort,
                (dstIp & 0xFF000000) >>> 24,
                (dstIp & 0x00FF0000) >>> 16,
                (dstIp & 0x0000FF00) >>> 8,
                (dstIp & 0x000000FF),
                dstPort,
                getInTotalPacketNum(), getOutTotalPacketNum(), packetInfos.size(),
                inPacketLenStdDeviation, outPacketLenStdDeviation,
                inAveragePacketLen, outAveragePacketLen,
                inPacketMaxIntervalTime, inPacketMinIntervalTime, inPacketAverageIntervalTime,
                outPacketMaxIntervalTime, outPacketMinIntervalTime, outPacketAverageIntervalTime,
                inOutPacketVolumeRatio, inOutPacketNumRatio
                );
    }
}
