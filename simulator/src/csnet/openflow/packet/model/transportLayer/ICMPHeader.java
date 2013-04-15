package csnet.openflow.packet.model.transportLayer;

public class ICMPHeader implements TransportHeader {

    public static final String ID = ICMP;
    public static final int HEADER_LENGTH = 8;
    
    public int type;
    public int code;

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
        StringBuilder ret = new StringBuilder("ICMP<");
        ret.append("tyep:");
        ret.append(type);
        ret.append(", ");
        ret.append("code:");
        ret.append(code);
        ret.append(">");
        return ret.toString();
    }
}
