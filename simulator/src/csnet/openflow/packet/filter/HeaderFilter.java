package csnet.openflow.packet.filter;

import java.util.HashSet;
import java.util.Set;

import csnet.openflow.packet.model.Packet;

public class HeaderFilter implements PacketFilter {
    private Set<String> networkIds;
    private Set<String> transportIds;

    private long total;
    private long permitted;

    public HeaderFilter() {
        networkIds = new HashSet<String>();
        transportIds = new HashSet<String>();
        total = 0;
        permitted = 0;
    }

    public void addPermitedNetworkId(String networkId) {
        networkIds.add(networkId);
    }

    public void addPermitedTransportId(String transportId) {
        transportIds.add(transportId);
    }

    @Override
    public boolean permitted(Packet packet) {
        total++;
        if (networkIds.contains(packet.getNetworkId())
                && transportIds.contains(packet.getTransportId())) {
            permitted++;
            return true;
        }
        return false;
    }
}
