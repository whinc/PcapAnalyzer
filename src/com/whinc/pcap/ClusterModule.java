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
        // 清除上次的数据
        netFlows.clear();

        // 汇聚网络流
        converge(data);


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

            if (!packet.hasHeader(ip4) || !packet.hasHeader(tcp)) continue;

            NetFlow netFlow = find(packetInfo);
            if (tcp.flags_SYN()) {      // SYN 作为TCP建立连接的标识，创建网络流
                if (netFlow == null) {
                    netFlow = new NetFlow(ip4.sourceToInt(), ip4.destinationToInt(),
                            tcp.source(), tcp.destination());
                    netFlow.add(packetInfo);
                    netFlows.add(netFlow);
                }
            } else if (tcp.flags_FIN()) {
                if (netFlow != null) {
                    netFlow.setClosed(true);        // FIN作为TCP结束标识，关闭网络流
                }
            } else {                    // 加入到已存在的网络流中
                if (netFlow != null && !netFlow.isClosed()) {
                    netFlow.add(packetInfo);
                }
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

    private NetFlow find(PacketInfo packetInfo) {
        NetFlow result = null;
        for (NetFlow netflow : netFlows) {
            if (netflow.contain(packetInfo)) {
                result = netflow;
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
