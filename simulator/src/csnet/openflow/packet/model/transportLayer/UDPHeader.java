package csnet.openflow.packet.model.transportLayer;

public class UDPHeader implements TransportHeader {

    public static final String ID = UDP;
    public static final int HEADER_LENGTH = 8;
    
    public int srcPort;
    public int dstPort;

    public static enum Field {

        srcPort, dstPort
    }

    public void set(Field field, int value) {
        switch (field) {
            case srcPort:
                srcPort = value;
                break;
            case dstPort:
                dstPort = value;
                break;
            default:
                throw new IllegalArgumentException("set udp field error!");
        }
    }

    public int get(Field field) {
        switch (field) {
            case srcPort:
                return srcPort;
            case dstPort:
                return dstPort;
            default:
                throw new IllegalArgumentException("get udp field error!");
        }
    }

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
        StringBuilder ret = new StringBuilder("UDP<");
        ret.append("srcP:");
        ret.append(srcPort);
        ret.append(", ");
        ret.append("dstP:");
        ret.append(dstPort);
        ret.append(">");
        return ret.toString();
    }
}
