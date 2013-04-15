package csnet.openflow.packet.comparator;

import java.util.Comparator;
import java.util.List;

import csnet.openflow.packet.model.Packet;
import csnet.openflow.packet.model.networkLayer.Ipv4Header;

public class Ipv4Comparator implements Comparator<Packet> {
    private Ipv4Header ipv4Mask;
    private List<Ipv4Header.Field> ipv4Fields;

    public Ipv4Comparator(List<Ipv4Header.Field> fields, Ipv4Header ipv4Mask) {
        this.ipv4Mask = ipv4Mask;
        this.ipv4Fields = fields;
    }

    @Override
    public int compare(Packet p1, Packet p2) {
        Ipv4Header ipv4Header1 = (Ipv4Header) p1.data.networkHeader;
        Ipv4Header ipv4Header2 = (Ipv4Header) p2.data.networkHeader;
        for (Ipv4Header.Field field : ipv4Fields) {
            int mask = ipv4Mask.get(field);
            int value = (ipv4Header1.get(field) & mask)
                    - (ipv4Header2.get(field) & mask);
            if (value != 0) {
                return value;
            }
        }
        return 0;
    }
}
