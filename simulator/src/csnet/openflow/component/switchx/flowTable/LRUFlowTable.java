package csnet.openflow.component.switchx.flowTable;

import csnet.openflow.logger.Logger;
import csnet.openflow.packet.model.Packet;
import csnet.openflow.util.NoOverridePriorityQueue;
import java.util.Comparator;

public class LRUFlowTable implements FlowTable {

    private NoOverridePriorityQueue<Packet> flowEntries;
    private long delay;
    private long bufferTime;
    private long flowIndex;
    private int maxSize;
    private Logger logger;

    public LRUFlowTable(Comparator<Packet> comparator) {
        flowEntries = new NoOverridePriorityQueue<Packet>(comparator);
        this.flowIndex = 0;
    }

    public void config(long bufferTime, long delay, int maxSize) {
        this.bufferTime = bufferTime;
        this.delay = delay;
        this.maxSize = maxSize;
    }

    @Override
    public void install(Packet packet) {
        long deadline = packet.getTimestamp() + bufferTime;
        Packet original = flowEntries.put(packet, deadline);
        if (original == null) {
            packet.statistic.flowIndex = ++flowIndex;
            packet.statistic.createTime = packet.getTimestamp();
            packet.statistic.deadline = deadline;
            logger.logFlowEntryCreate(packet);
        } else {
            original.statistic.deadline = deadline;
            packet.statistic.flowIndex = original.statistic.flowIndex;
        }
        while (flowEntries.size() > maxSize) {
            Packet removed = flowEntries.pollMin();
            removed.statistic.deadline = packet.getTimestamp();
            logger.logFlowEntryRemove(removed);
        }
    }

    @Override
    public boolean contains(Packet packet) {
        Packet original = flowEntries.get(packet);
        if (original == null) {
            return false;
        }
        long deadline = packet.getTimestamp() + bufferTime;
        if (deadline > original.statistic.deadline) {
            original.statistic.deadline = deadline;
            flowEntries.put(original, deadline);
        }
        packet.statistic.flowIndex = original.statistic.flowIndex;
        return true;
    }

    @Override
    public boolean containsAndDelay(Packet packet) {
        packet.setTimestamp(packet.getTimestamp() + delay);
        return contains(packet);
    }

    @Override
    public void clear(long timestamp) {
        while (!flowEntries.isEmpty()) {
            Packet packet = flowEntries.peekMin();
            if (packet.statistic.deadline < timestamp) {
                flowEntries.pollMin();
                logger.logFlowEntryRemove(packet);
            } else {
                break;
            }
        }
    }

    @Override
    public int size() {
        return flowEntries.size();
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
