package csnet.openflow.component.switchx.flowEntryDelay;

import java.util.Iterator;
import java.util.LinkedList;

import csnet.openflow.component.link.Link;
import csnet.openflow.component.switchx.flowTable.FlowTable;
import csnet.openflow.packet.model.Packet;

abstract public class AbstractFlowEntryDelay implements FlowEntryDelay {

    private Link fromController;
    private Link toFlowTable;
    protected FlowTable flowTable;
    private double thresholdTime;
    private long currentTime;
    private int maxBufferSize;
    private int bufferSize;
    private LinkedList<Packet> buffer;

    public AbstractFlowEntryDelay(FlowTable flowTable) {
        this.flowTable = flowTable;
        this.bufferSize = 0;
        this.buffer = new LinkedList<>();
    }

    public void config(int bufferSize) {
        this.maxBufferSize = bufferSize;
    }

    @Override
    public void connect(Link fromController, Link toFlowTable) {
        this.fromController = fromController;
        this.toFlowTable = toFlowTable;
    }

    ;

	private void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
        fromController.setCurrentTime(currentTime);
    }

    private void sendBufferedPacket() {
        Iterator<Packet> iterator = buffer.iterator();
        while (iterator.hasNext()) {
            Packet packet = iterator.next();
            if (sendPacket(packet)) {
                bufferSize -= packet.getSize(Packet.SizeType.header);
                iterator.remove();
            } else {
                break;
            }
        }
    }

    private boolean bufferPacket(Packet packet) {
        int newSize = packet.getSize(Packet.SizeType.header) + bufferSize;
        if (newSize <= maxBufferSize) {
            buffer.add(packet);
            bufferSize = newSize;
            return true;
        }
        return false;
    }

    private boolean sendPacket(Packet packet) {
        thresholdTime = Math.max(packet.getTimestamp(), thresholdTime);
        if (thresholdTime < currentTime) {
            if (flowTable.contains(packet)) {
                return true;
            } else {
                double waiting = getWaitingTime();
                if (thresholdTime + waiting < currentTime) {
                    thresholdTime += waiting;
                    packet.setTimestamp((long) thresholdTime);
                    toFlowTable.send(packet);
                    return true;
                }
            }
        }
        return false;
    }

    abstract protected double getWaitingTime();

    @Override
    public boolean schedule(long timestamp) {
        setCurrentTime(timestamp);
        sendBufferedPacket();
        Packet packet = null;
        while ((packet = fromController.receive()) != null) {
            if (!sendPacket(packet)) {
                do {
                    if (!bufferPacket(packet)) {
                        ;
                    }
                } while ((packet = fromController.receive()) != null);
                break;
            }
        }
        return false;
    }
}
