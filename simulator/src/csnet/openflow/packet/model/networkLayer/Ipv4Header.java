package csnet.openflow.packet.model.networkLayer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ipv4Header implements NetworkHeader {

    public static final String ID = IPV4;
    public static final int HEADER_LENGTH = 20;
    
    public int tos;
    public int id;
    public int flags;
    public int ttl;
    public byte[] src = new byte[4];
    public byte[] dst = new byte[4];

    public static enum Field {

        tos, id, flags, ttl,
        src0, src1, src2, src3,
        dst0, dst1, dst2, dst3
    }

    public void set(Field field, int value) {
        switch (field) {
            case tos:
                tos = value;
                break;
            case id:
                id = value;
                break;
            case flags:
                flags = value;
                break;
            case ttl:
                ttl = value;
                break;
            case src0:
                src[0] = (byte) value;
                break;
            case src1:
                src[1] = (byte) value;
                break;
            case src2:
                src[2] = (byte) value;
                break;
            case src3:
                src[3] = (byte) value;
                break;
            case dst0:
                dst[0] = (byte) value;
                break;
            case dst1:
                dst[1] = (byte) value;
                break;
            case dst2:
                dst[2] = (byte) value;
                break;
            case dst3:
                dst[3] = (byte) value;
                break;
            default:
                throw new IllegalArgumentException("set ipv4 field error!");
        }
    }

    public int get(Field field) {
        switch (field) {
            case tos:
                return tos;
            case id:
                return id;
            case flags:
                return flags;
            case ttl:
                return ttl;
            case src0:
                return src[0];
            case src1:
                return src[1];
            case src2:
                return src[2];
            case src3:
                return src[3];
            case dst0:
                return dst[0];
            case dst1:
                return dst[1];
            case dst2:
                return dst[2];
            case dst3:
                return dst[3];
            default:
                throw new IllegalArgumentException("get ipv4 field error!");
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
        StringBuilder ret = new StringBuilder("IP4<");
        ret.append("src:");
        try {
            ret.append(InetAddress.getByAddress(src).getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ret.append(", ");
        ret.append("dst:");
        try {
            ret.append(InetAddress.getByAddress(dst).getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ret.append(", ");
        ret.append("tos:");
        ret.append(tos);
        ret.append(", ");
        ret.append("id:");
        ret.append(id);
        ret.append(", ");
        ret.append("flags:");
        ret.append(flags);
        ret.append(", ");
        ret.append("ttl:");
        ret.append(ttl);
        ret.append(">");
        return ret.toString();
    }
}
