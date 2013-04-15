package csnet.openflow.packet.model;

import csnet.openflow.packet.model.networkLayer.NetworkHeader;
import csnet.openflow.packet.model.transportLayer.TransportHeader;

/**
 * 数据层报文数据类，用于存储报文中的数据（报文大小、网络头和传输头）
 *
 * @author kfirst
 *
 */
public class PacketData {

    public NetworkHeader networkHeader;
    public TransportHeader transportHeader;
    int octets;
    int headerOctets;

    /**
     * 通过报文的原始长度、网络头和传输头构造一个报文
     *
     * @param octets 报文的原始长度
     * @param networkHeader 网络头
     * @param transportHeader 传输头
     */
    public PacketData(int octets, NetworkHeader networkHeader,
            TransportHeader transportHeader) {
        this.octets = octets;
        this.networkHeader = networkHeader;
        this.transportHeader = transportHeader;
        this.headerOctets = networkHeader.getHeaderLength()
                + transportHeader.getHeaderLength();
    }

    /**
     * 获取网络头的ID，通过该ID可以获知网络层的报文类型
     *
     * @return 网络头的ID
     */
    public String getNetworkId() {
        return networkHeader.getID();
    }

    /**
     * 获取传输头的ID，通过该ID可以获知传输层的报文类型
     *
     * @return 输头的ID
     */
    public String getTransportId() {
        return transportHeader.getID();
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("Data layer packet, ");
        ret.append("octets: ");
        ret.append(octets);
        ret.append("\n");
        ret.append("[Net] ");
        ret.append(networkHeader.toString());
        ret.append("\n");
        ret.append("[Tra] ");
        ret.append(transportHeader.toString());
        return ret.toString();
    }
}
