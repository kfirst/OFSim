package csnet.openflow.component.controller;

import csnet.openflow.component.interfacex.Interface;
import csnet.openflow.logger.Logger;

public interface Controller extends Interface {

    public void setLogger(Logger logger);
}
