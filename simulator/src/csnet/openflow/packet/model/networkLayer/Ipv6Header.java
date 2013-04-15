package csnet.openflow.packet.model.networkLayer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ipv6Header implements NetworkHeader {

    public static final String ID = IPV6;
    public static final int HEADER_LENGTH = 40;
    
    public int trafficClass;
    public int flowLabel;
    public int hopLimit;
    public byte[] source = new byte[16];
    public byte[] destination = new byte[16];

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public int getHeaderLength() {
        return HEADER_LENGTH;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("IP6<");
        ret.append("src:");
        try {
            ret.append(InetAddress.getByAddress(source).getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ret.append(", ");
        ret.append("dst");
        try {
            ret.append(InetAddress.getByAddress(destination).getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ret.append(", ");
        ret.append("trafficClass:");
        ret.append(trafficClass);
        ret.append(", ");
        ret.append("flowLabel:");
        ret.append(flowLabel);
        ret.append(", ");
        ret.append("hopLimit:");
        ret.append(hopLimit);
        ret.append(">");
        return ret.toString();
    }
}
