package csnet.openflow.packet.comparator;

import java.util.Comparator;
import java.util.List;

import csnet.openflow.packet.model.Packet;
import csnet.openflow.packet.model.transportLayer.UDPHeader;

public class UdpComparator implements Comparator<Packet> {
    private UDPHeader udpMask;
    private List<UDPHeader.Field> udpFields;

    public UdpComparator(List<UDPHeader.Field> fields, UDPHeader udpMask) {
        this.udpMask = udpMask;
        this.udpFields = fields;
    }

    public void addUdpField(UDPHeader.Field field) {
        udpFields.add(field);
    }

    @Override
    public int compare(Packet p1, Packet p2) {
        UDPHeader udpHeader1 = (UDPHeader) p1.data.transportHeader;
        UDPHeader udpHeader2 = (UDPHeader) p2.data.transportHeader;
        for (UDPHeader.Field field : udpFields) {
            int mask = udpMask.get(field);
            int value = (udpHeader1.get(field) & mask)
                    - (udpHeader2.get(field) & mask);
            if (value != 0) {
                return value;
            }
        }
        return 0;
    }
}
