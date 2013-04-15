package csnet.openflow.packet.comparator;

import java.util.Comparator;
import java.util.List;

import csnet.openflow.packet.model.Packet;
import csnet.openflow.packet.model.transportLayer.TCPHeader;

public class TcpComparator implements Comparator<Packet> {
    private TCPHeader tcpMask;
    private List<TCPHeader.Field> tcpFields;

    public TcpComparator(List<TCPHeader.Field> fields, TCPHeader tcpMask) {
        this.tcpMask = tcpMask;
        this.tcpFields = fields;
    }

    public void addUdpField(TCPHeader.Field field) {
        tcpFields.add(field);
    }

    @Override
    public int compare(Packet p1, Packet p2) {
        TCPHeader tcpHeader1 = (TCPHeader) p1.data.transportHeader;
        TCPHeader tcpHeader2 = (TCPHeader) p2.data.transportHeader;
        for (TCPHeader.Field field : tcpFields) {
            long mask = tcpMask.get(field);
            long value = (tcpHeader1.get(field) & mask)
                    - (tcpHeader2.get(field) & mask);
            if (value > 0) {
                return 1;
            } else if (value < 0) {
                return -1;
            }
        }
        return 0;
    }
}
