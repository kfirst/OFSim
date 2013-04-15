package csnet.openflow.component.link;

import csnet.openflow.packet.model.Packet;

/**
 * 常量时延link，当报文通过该link时，会产生常量的时延
 * <p />
 * <strong>另外注意：</strong><br />
 * 1. 每个link均为单向链路，双向的链路需要使用一对link；<br />
 * 2. link的缓存是保序的，不会改变报文的顺序
 * 
 * @author kfirst
 * 
 */
public class ConstantLatencyLink extends AbstractLink {
    private int latency;

    public ConstantLatencyLink(int latency) {
        super(Category.LIST);
        this.latency = latency;
    }

    public ConstantLatencyLink(Category category, int latency) {
        super(category);
        this.latency = latency;
    }

    @Override
    protected void changePacket(Packet packet) {
        packet.statistic.timestamp += latency;
    }
}
