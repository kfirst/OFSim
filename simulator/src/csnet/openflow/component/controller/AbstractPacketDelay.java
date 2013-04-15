package csnet.openflow.component.controller;

import csnet.openflow.component.interfacex.Interface;
import csnet.openflow.component.link.Link;
import csnet.openflow.packet.model.Packet;
import java.util.Iterator;
import java.util.LinkedList;

abstract public class AbstractPacketDelay implements Interface {

    protected Link input;
    protected Link output;
    protected double thresholdTime;
    protected long currentTime;
    protected int maxBufferSize;
    protected int bufferSize;
    protected LinkedList<Packet> buffer;

    public AbstractPacketDelay() {
        this.bufferSize = 0;
        this.buffer = new LinkedList<>();
    }

    public void config(int bufferSize) {
        this.maxBufferSize = bufferSize;
    }

    @Override
    public void connect(Link input, Link output) {
        this.input = input;
        this.output = output;
    }

    protected void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
        input.setCurrentTime(currentTime);
    }

    protected void sendBufferedPacket() {
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

    protected boolean bufferPacket(Packet packet) {
        int newSize = packet.getSize(Packet.SizeType.header) + bufferSize;
        if (newSize <= maxBufferSize) {
            buffer.add(packet);
            bufferSize = newSize;
            return true;
        }
        return false;
    }

    protected boolean sendPacket(Packet packet) {
        thresholdTime = Math.max(packet.getTimestamp(), thresholdTime);
        if (Double.isNaN(packet.statistic.controller_delay)) {
            packet.statistic.controller_delay = getWaitingTime();
        }
        double waiting = packet.statistic.controller_delay;
        if (thresholdTime + waiting < currentTime) {
            thresholdTime += waiting;
            packet.setTimestamp((long) thresholdTime);
            recordPacketOut(packet);
            output.send(packet);
            return true;
        }
        return false;
    }

    abstract protected double getWaitingTime();

    abstract protected void recordPacketIn(Packet packet);

    abstract protected void recordPacketOut(Packet packet);

    @Override
    public boolean schedule(long timestamp) {
        setCurrentTime(timestamp);
        sendBufferedPacket();
        Packet packet = null;
        while ((packet = receive()) != null) {
            if (buffer.isEmpty() && sendPacket(packet)) {
                ;
            } else {
                do {
                    if (!bufferPacket(packet)) {
                        ;
                    }
                } while ((packet = receive()) != null);
                break;
            }
        }
        return false;
    }

    private Packet receive() {
        Packet packet = input.receive();
        if (packet != null) {
            packet.statistic.controller_delay = Double.NaN;
            recordPacketIn(packet);
        }
        return packet;
    }
}
