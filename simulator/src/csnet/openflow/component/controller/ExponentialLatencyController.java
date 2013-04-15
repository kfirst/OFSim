package csnet.openflow.component.controller;

import csnet.openflow.logger.Logger;
import csnet.openflow.packet.model.Packet;
import csnet.openflow.util.Distribute;

public class ExponentialLatencyController extends AbstractPacketDelay implements
        Controller {

    private int averageLatency;
    private Logger logger;

    public ExponentialLatencyController() {
        super();
    }

    public void config(int bufferSize, int averageLatency) {
        config(bufferSize);
        this.averageLatency = averageLatency;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected double getWaitingTime() {
        return Distribute.exponential(averageLatency);
    }

    @Override
    protected void recordPacketIn(Packet packet) {
        logger.logPacketOfControllerIn(packet);
    }

    @Override
    protected void recordPacketOut(Packet packet) {
        logger.logPacketOfControllerOut(packet);
    }
}
