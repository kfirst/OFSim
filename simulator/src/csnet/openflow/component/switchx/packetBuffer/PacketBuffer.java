package csnet.openflow.component.switchx.packetBuffer;

import csnet.openflow.packet.model.Packet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author kfirst
 */
public class PacketBuffer {

    private Map<Packet, Set<LinkedListNode<Packet>>> index;
    private LinkedListNode<Packet> buffer;
    private int maxSize;
    private int size;

    public PacketBuffer(Comparator<Packet> comparator) {
        this.size = 0;
        this.index = new TreeMap<>(comparator);
        this.buffer = null;
    }

    public void config(int size) {
        this.maxSize = size;
    }

    public boolean add(Packet packet) {
        int packetSize = packet.getSize(Packet.SizeType.total);
        while (size + packetSize > maxSize) {
            if (!removeOldestPacket()) {
                return false;
            }
        }
        addNewPacket(packet);
        return true;
    }

    public List<Packet> remove(Packet packet) {
        Set<LinkedListNode<Packet>> packets = index.remove(packet);
        List<Packet> ret = new ArrayList<>();
        if (packets != null && !packets.isEmpty()) {
            for (LinkedListNode<Packet> node : packets) {
                Packet p = node.getData();
                if (node == buffer) {
                    buffer = node.remove();
                } else {
                    node.remove();
                }
                ret.add(p);
                size -= p.getSize(Packet.SizeType.total);
            }
        }
        return ret;
    }

    private boolean removeOldestPacket() {
        if (buffer == null) {
            return false;
        }
        Packet packet = buffer.getData();
        index.get(packet).remove(buffer);
        buffer = buffer.remove();
        size -= packet.getSize(Packet.SizeType.total);
        return true;
    }

    private void addNewPacket(Packet packet) {
        size += packet.getSize(Packet.SizeType.total);
        LinkedListNode<Packet> node = new LinkedListNode<>(packet);
        if (buffer == null) {
            buffer = node;
        } else {
            buffer.addPre(node);
        }
        Set<LinkedListNode<Packet>> packets = index.get(packet);
        if (packets == null) {
            packets = new LinkedHashSet<>();
            index.put(packet, packets);
        }
        packets.add(node);
    }
}

class LinkedListNode<E> {

    private LinkedListNode pre;
    private LinkedListNode next;
    private E data;

    public LinkedListNode(E data) {
        pre = this;
        next = this;
        this.data = data;
    }

    public E getData() {
        return data;
    }

    public LinkedListNode<E> remove() {
        if (next == this) {
            return null;
        }
        pre.next = next;
        next.pre = pre;
        return next;
    }

    public void addPre(LinkedListNode<E> node) {
        pre.next = node;
        node.pre = pre;
        pre = node;
        node.next = this;
    }

    public void addNext(LinkedListNode<E> node) {
        next.pre = node;
        node.next = next;
        next = node;
        node.pre = this;
    }
}