package csnet.openflow.component.link;

import csnet.openflow.packet.model.Packet;

/**
 * link接口，各种类型的link均需实现此接口
 * <p />
 * link的主要作用为：<br />
 * 1. 缓存接收到的报文，并将报文交给下级设备；<br />
 * 2. 进行报文通过link时需要作出的改变（如改动时间戳，以便模拟传输时延）；
 * 
 * @author kfirst
 * 
 */
public interface Link {
    /**
     * 向link发送一个报文，该报文会暂时保存在link的缓存中
     * 
     * @param packet
     *            要发送的报文
     */
    public void send(Packet packet);

    /**
     * 设置当前时间
     * 
     * @param timestampInMicrosecond
     *            当前时间
     */
    public void setCurrentTime(long timestampInMicrosecond);

    /**
     * 按发送的顺序从link缓存中取出时间小于当前时间报文，并将该报文从缓存中去除
     * 
     * @return
     */
    public Packet receive();

    /**
     * 清除时间小于当前时间的报文
     */
    public void clear();
}
