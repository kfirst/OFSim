package csnet.openflow.component.switchx.flowTable;

import csnet.openflow.logger.Logger;
import csnet.openflow.packet.model.Packet;

public interface FlowTable {

    public void install(Packet packet);

    public boolean contains(Packet packet);

    public boolean containsAndDelay(Packet packet);

    public void clear(long timestamp);

    public int size();

    public void setLogger(Logger logger);
}
