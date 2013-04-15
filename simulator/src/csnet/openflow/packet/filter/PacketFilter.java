package csnet.openflow.packet.filter;

import csnet.openflow.packet.model.Packet;

public interface PacketFilter {
    public boolean permitted(Packet packet);
}
