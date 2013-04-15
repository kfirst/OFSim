package csnet.openflow.packet.model.networkLayer;

import csnet.openflow.packet.model.Header;

abstract public interface NetworkHeader extends Header {

    public static final String IPV4 = "ipv4";
    public static final String IPV6 = "ipv6";

    abstract public int getHeaderLength();
}
