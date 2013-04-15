package csnet.openflow.packet.model.transportLayer;

public class TCPHeader implements TransportHeader {

    public static final String ID = TCP;
    public static final int HEADER_LENGTH = 20;
    
    public int srcPort;
    public int dstPort;
    public long ack;
    public long seq;

    public static enum Field {

        srcPort, dstPort, ack, seq
    }

    public void set(Field field, long value) {
        switch (field) {
            case srcPort:
                srcPort = (int) value;
                break;
            case dstPort:
                dstPort = (int) value;
                break;
            case ack:
                ack = value;
                break;
            case seq:
                seq = value;
                break;
            default:
                throw new IllegalArgumentException("set tcp field error!");
        }
    }

    public long get(Field field) {
        switch (field) {
            case srcPort:
                return srcPort;
            case dstPort:
                return dstPort;
            case ack:
                return ack;
            case seq:
                return seq;
            default:
                throw new IllegalArgumentException("get tcp field error!");
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
        StringBuilder ret = new StringBuilder("TCP<");
        ret.append("srcP:");
        ret.append(srcPort);
        ret.append(", ");
        ret.append("dstP:");
        ret.append(dstPort);
        ret.append(", ");
        ret.append("ack:");
        ret.append(ack);
        ret.append(", ");
        ret.append("seq:");
        ret.append(seq);
        ret.append(">");
        return ret.toString();
    }
}
