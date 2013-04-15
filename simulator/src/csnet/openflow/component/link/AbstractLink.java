package csnet.openflow.component.link;

import csnet.openflow.packet.model.Packet;
import csnet.openflow.util.MinQueue;
import csnet.openflow.util.Queue;
import csnet.openflow.util.StackQueue;

/**
 * 基本link类，其他link需要继承该类。
 * <p />
 * 该类实现了link接口的所有方法，并且预留了一个抽象方法{@link AbstractLink#changePacket}，
 * 报文通过link时需要进行的修改可以写在这个方法中，子类只需实现该方法即可。
 * 
 * @author kfirst
 * 
 */
public abstract class AbstractLink implements Link {
    private Queue<Packet> packetsInLink;
    private long currentTime;

    public enum Category {
        LIST, HEAP
    };

    public AbstractLink() {
        packetsInLink = new StackQueue<Packet>();
    }

    public AbstractLink(Category category) {
        if (Category.LIST == category) {
            packetsInLink = new StackQueue<Packet>();
        } else if (Category.HEAP == category) {
            packetsInLink = new MinQueue<Packet>();
        }
    }

    @Override
    public void send(Packet packet) {
        changePacket(packet);
        packetsInLink.add(packet);
    }

    @Override
    public void setCurrentTime(long timestampInMicrosecond) {
        currentTime = timestampInMicrosecond;
    }

    @Override
    public Packet receive() {
        Packet packet = packetsInLink.peek();
        if (packet != null && packet.getTimestamp() < currentTime) {
            return packetsInLink.poll();
        }
        return null;
    }

    @Override
    public void clear() {
        Packet packet = null;
        while ((packet = packetsInLink.peek()) != null) {
            if (packet.getTimestamp() < currentTime) {
                packetsInLink.poll();
            } else {
                break;
            }
        }
    }

    /**
     * 用于实现报文经过该link时需要作出的修改，当报文插入link缓存时会被调用
     * <p />
     * 子类需要实现该方法进行自己的报文修改
     * 
     * @param packet
     */
    protected abstract void changePacket(Packet packet);
}
