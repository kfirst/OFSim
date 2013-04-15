package csnet.openflow.packet.comparator;

import java.util.Comparator;
import java.util.TreeMap;

import csnet.openflow.packet.model.Packet;

public class PacketComparator implements Comparator<Packet> {
    private TreeMap<String, Comparator<Packet>> networkComparators;
    private TreeMap<String, Comparator<Packet>> transportComparators;

    public PacketComparator() {
        networkComparators = new TreeMap<String, Comparator<Packet>>();
        transportComparators = new TreeMap<String, Comparator<Packet>>();
    }

    public void addNetwordComparator(String networkId,
            Comparator<Packet> comparator) {
        networkComparators.put(networkId, comparator);
    }

    public void addTransportComparator(String transportId,
            Comparator<Packet> comparator) {
        transportComparators.put(transportId, comparator);
    }

    @Override
    public int compare(Packet p1, Packet p2) {
        if (!networkComparators.isEmpty()) {
            int ret = p1.getNetworkId().compareTo(p2.getNetworkId());
            if (ret != 0) {
                return ret;
            }
            ret = networkComparators.get(p1.getNetworkId()).compare(p1, p2);
            if (ret != 0) {
                return ret;
            }
        }
        if (!transportComparators.isEmpty()) {
            int ret = p1.getTransportId().compareTo(p2.getTransportId());
            if (ret != 0) {
                return ret;
            }
            ret = transportComparators.get(p1.getTransportId()).compare(p1, p2);
            if (ret != 0) {
                return ret;
            }
        }
        return 0;
    }

}
