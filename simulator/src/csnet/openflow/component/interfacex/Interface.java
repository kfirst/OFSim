package csnet.openflow.component.interfacex;

import csnet.openflow.component.Device;
import csnet.openflow.component.link.Link;

public interface Interface extends Device {

    public void connect(Link input, Link output);
}
