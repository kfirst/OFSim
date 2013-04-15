package csnet.openflow.packet.model;

public class Packet implements Comparable<Packet> {

    public static enum SizeType {

        total, header, one
    }
    /**
     * 报文包含的数据
     */
    public PacketData data;
    /**
     * 报文包含的统计信息
     */
    public PacketStatistic statistic;

    public int getSize(SizeType type) {
        switch (type) {
            case total:
                return data.octets;
            case header:
                return data.headerOctets;
            case one:
                return 1;
        }
        return 0;
    }

    public String getNetworkId() {
        return data.getNetworkId();
    }

    public String getTransportId() {
        return data.getTransportId();
    }

    public long getTimestamp() {
        return statistic.timestamp;
    }

    public void setTimestamp(long timestamp) {
        statistic.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(data.toString());
        ret.append("\n");
        ret.append(statistic.toString());
        return ret.toString();
    }

    @Override
    public int compareTo(Packet o) {
        return statistic.compareTo(o.statistic);
    }
}
