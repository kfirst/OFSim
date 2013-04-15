package csnet.openflow.component.switchx.flowEntryDelay;

import csnet.openflow.component.Device;
import csnet.openflow.component.link.Link;

public interface FlowEntryDelay extends Device {

    public void connect(Link fromController, Link toFlowTable);
}
