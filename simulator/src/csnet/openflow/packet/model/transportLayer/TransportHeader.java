package csnet.openflow.packet.model.transportLayer;

import csnet.openflow.packet.model.Header;

public interface TransportHeader extends Header {

    public static final String ICMP = "icmp";
    public static final String UDP = "udp";
    public static final String TCP = "tcp";

    abstract public int getHeaderLength();
}
