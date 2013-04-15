package csnet.openflow.component.interfacex;

import csnet.openflow.component.link.Link;
import csnet.openflow.packet.model.Packet;
import java.util.Iterator;
import java.util.LinkedList;

public class BandLimitedPostponeInterface implements Interface {

    private Link output;
    private Link input;
    private double thresholdTime;
    private double timePerByte;
    private long currentTime;
    private int maxBufferSize;
    private int bufferSize;
    private Packet.SizeType type;
    private LinkedList<Packet> buffer;

    public BandLimitedPostponeInterface() {
        this.thresholdTime = 0;
        this.bufferSize = 0;
        this.buffer = new LinkedList<Packet>();
    }

    public void config(long bandwidth, int bufferSize, Packet.SizeType type) {
        if (bandwidth == 0) {
            this.timePerByte = 0;
        } else {
            this.timePerByte = 1.0 * 1000 * 1000 / bandwidth;
        }
        this.maxBufferSize = bufferSize;
        this.type = type;
    }

    @Override
    public void connect(Link input, Link output) {
        this.input = input;
        this.output = output;
    }

    private void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
        input.setCurrentTime(currentTime);
    }

    private void sendBufferedPacket() {
        Iterator<Packet> iterator = buffer.iterator();
        while (iterator.hasNext()) {
            Packet packet = iterator.next();
            if (sendPacket(packet)) {
                bufferSize -= packet.getSize(type);
                iterator.remove();
            } else {
                break;
            }
        }
    }

    private boolean bufferPacket(Packet packet) {
        int newSize = packet.getSize(type) + bufferSize;
        if (newSize <= maxBufferSize) {
            buffer.add(packet);
            bufferSize = newSize;
            return true;
        }
        return false;
    }

    private boolean sendPacket(Packet packet) {
        thresholdTime = Math.max(packet.getTimestamp(), thresholdTime);
        if (thresholdTime <= currentTime) {
            packet.setTimestamp((long) thresholdTime);
            thresholdTime += packet.getSize(type) * timePerByte;
            output.send(packet);
            return true;
        }
        return false;
    }

    @Override
    public boolean schedule(long timestamp) {
        setCurrentTime(timestamp);
        sendBufferedPacket();
        Packet packet = null;
        while ((packet = input.receive()) != null) {
            if (buffer.isEmpty() && sendPacket(packet)) {
                ;
            } else {
                do {
                    if (!bufferPacket(packet)) {
                        ;
                    }
                } while ((packet = input.receive()) != null);
                break;
            }
        }
        return false;
    }
}
