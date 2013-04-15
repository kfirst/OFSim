package csnet.openflow.logger;

import csnet.openflow.packet.model.Packet;

public interface Logger {

    public void logPacketOfGenerator(Packet packet);

    public void logPacketOfReceiver(Packet packet);

    public void logPacketOfControllerIn(Packet packet);

    public void logPacketOfControllerOut(Packet packet);

    public void logFlowEntryCreate(Packet packet);

    public void logFlowEntryRemove(Packet packet);

    public void close();
}
