package csnet.openflow.component.packetGenerator;

import csnet.openflow.component.Device;
import csnet.openflow.component.link.Link;
import csnet.openflow.logger.Logger;
import csnet.openflow.packet.extractor.PcapPacketExtractor;
import csnet.openflow.packet.filter.PacketFilter;
import csnet.openflow.packet.model.Packet;

public class PcapGenerator implements Device {

    private PacketFilter filter;
    private PcapPacketExtractor extractor;
    private Link output;
    private long packetIndex;
    private Logger logger;
    private boolean stop;

    public PcapGenerator() {
        extractor = new PcapPacketExtractor();
        packetIndex = 0;
        stop = false;
    }

    public void config(PacketFilter filter) {
        this.filter = filter;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void connect(Link output) {
        this.output = output;
    }

    public void addPcap(String pcapPath) {
        extractor.add(pcapPath);
    }

    public long getStartTime() {
        Packet first = extractor.peekPacket();
        if (first == null) {
            return -1;
        }
        return first.getTimestamp();
    }
    
    public void stop() {
        this.stop = true;
    }

    @Override
    public boolean schedule(long timestamp) {
        Packet packet = null;
        while ((packet = extractor.peekPacket()) != null && !stop) {
            if (packet.getTimestamp() >= timestamp) {
                return true;
            }
            packet = extractor.pollPacket();
            if (filter.permitted(packet)) {
                packet.statistic.packetIndex = ++packetIndex;
                output.send(packet);
                recordPacket(packet);
            }
        }
        return false;
    }

    private void recordPacket(Packet packet) {
        logger.logPacketOfGenerator(packet);
    }
}
