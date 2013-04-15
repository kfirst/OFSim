package csnet.openflow.packet.extractor;

import org.jnetpcap.PcapHeader;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import csnet.openflow.packet.model.Packet;
import csnet.openflow.packet.model.PacketData;
import csnet.openflow.packet.model.PacketStatistic;
import csnet.openflow.packet.model.networkLayer.Ipv4Header;
import csnet.openflow.packet.model.networkLayer.Ipv6Header;
import csnet.openflow.packet.model.networkLayer.NetworkHeader;
import csnet.openflow.packet.model.transportLayer.ICMPHeader;
import csnet.openflow.packet.model.transportLayer.TCPHeader;
import csnet.openflow.packet.model.transportLayer.TransportHeader;
import csnet.openflow.packet.model.transportLayer.UDPHeader;

public class PacketConverter {
    private Ip4 ip4;
    private Ip6 ip6;
    private Tcp tcp;
    private Udp udp;
    private Icmp icmp;

    public PacketConverter() {
        ip4 = new Ip4();
        ip6 = new Ip6();
        tcp = new Tcp();
        udp = new Udp();
        icmp = new Icmp();
    }

    public Packet convert(PcapPacket pcapPacket) {
        scanPacket(pcapPacket);

        PcapHeader header = pcapPacket.getCaptureHeader();

        NetworkHeader networkHeader = convertNetworkHeader(pcapPacket);
        TransportHeader transportHeader = convertTransportHeader(pcapPacket);
        if (networkHeader == null || transportHeader == null) {
            return null;
        }
        PacketData data = new PacketData(header.wirelen(), networkHeader,
                transportHeader);

        PacketStatistic statistic = new PacketStatistic();
        statistic.timestamp = header.timestampInMicros();

        Packet packet = new Packet();
        packet.data = data;
        packet.statistic = statistic;
        return packet;
    }

    private void scanPacket(PcapPacket pcapPacket) {
        if (pcapPacket.getHeaderCount() == 0) {
            pcapPacket.scan(Ip4.ID);
        }
        if (pcapPacket.getHeaderCount() == 0) {
            pcapPacket.scan(Ip6.ID);
        }
    }

    private NetworkHeader convertNetworkHeader(PcapPacket pcapPacket) {
        NetworkHeader header = null;
        if (pcapPacket.hasHeader(ip4))
            header = convertIpv4Header(ip4);
        else if (pcapPacket.hasHeader(ip6))
            header = convertIpv6Header(ip6);
        return header;
    }

    private Ipv4Header convertIpv4Header(Ip4 ip4) {
        Ipv4Header header = new Ipv4Header();
        header.tos = ip4.tos();
        header.id = ip4.id();
        header.flags = ip4.flags();
        header.ttl = ip4.ttl();
        header.src = ip4.source();
        header.dst = ip4.destination();
        return header;
    }

    private Ipv6Header convertIpv6Header(Ip6 ip6) {
        Ipv6Header header = new Ipv6Header();
        header.trafficClass = ip6.trafficClass();
        header.flowLabel = ip6.flowLabel();
        header.hopLimit = ip6.hopLimit();
        header.source = ip6.source();
        header.destination = ip6.destination();
        return header;
    }

    private TransportHeader convertTransportHeader(PcapPacket pcapPacket) {
        TransportHeader header = null;
        if (pcapPacket.hasHeader(udp))
            header = convertUdpHeader(udp);
        else if (pcapPacket.hasHeader(tcp))
            header = convertTcpHeader(tcp);
        else if (pcapPacket.hasHeader(icmp))
            header = convertIcmpHeader(icmp);
        return header;
    }

    private UDPHeader convertUdpHeader(Udp udp) {
        UDPHeader header = new UDPHeader();
        header.srcPort = udp.source();
        header.dstPort = udp.destination();
        return header;
    }

    private TCPHeader convertTcpHeader(Tcp tcp) {
        TCPHeader header = new TCPHeader();
        header.srcPort = tcp.source();
        header.dstPort = tcp.destination();
        header.seq = tcp.seq();
        header.ack = tcp.ack();
        return header;
    }

    private ICMPHeader convertIcmpHeader(Icmp icmp) {
        ICMPHeader header = new ICMPHeader();
        header.type = icmp.type();
        header.code = icmp.code();
        return header;
    }
}
