package com.whinc.pcap;

import com.whinc.model.NetFlow;
import com.whinc.model.PacketInfo;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/18.
 */
public class ClusterModule {
    private List<NetFlow> netFlows = new ArrayList<>();

    private static final ClusterModule singleton = new ClusterModule();

    public static ClusterModule getInstance() {
        return singleton;
    }

    public void extractVector(List<PacketInfo> data) {
        converge(data);

        // 打印输出
//        System.out.println(this);
    }

    /**
     * 将数据包汇聚成网络流
     * @param data
     */
    private void converge(List<PacketInfo> data) {
        for (PacketInfo packetInfo : data) {
            PcapPacket packet = packetInfo.getPacket();
            Ip4 ip4 = new Ip4();
            Tcp tcp = new Tcp();
            if (!addToNetFlow(packetInfo)
                    && packet.hasHeader(ip4)
                    && packet.hasHeader(tcp)) {
                NetFlow netFlow = new NetFlow(ip4.sourceToInt(), ip4.destinationToInt(),
                        tcp.source(), tcp.destination());
                netFlow.add(packetInfo);
                netFlows.add(netFlow);
            }
        }
    }

    /**
     * 判断传入的数据包是否属于已存在的某个网络流
     * @param packetInfo
     * @return
     */
    private boolean addToNetFlow(PacketInfo packetInfo) {
        boolean result = false;
        for (NetFlow netFlow : netFlows) {
            if (netFlow.contain(packetInfo)) {
                netFlow.add(packetInfo);
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (NetFlow netFlow : netFlows) {
            builder.append(netFlow).append("\n");
        }
        return builder.toString();
    }
}
