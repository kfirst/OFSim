package csnet.openflow.packet.model;

import csnet.openflow.util.Formater;

public class PacketStatistic implements Comparable<PacketStatistic> {

    /**
     * 时间戳，用于记录报文最近一次被处理或者将要被处理的时间
     */
    public long timestamp;
    /**
     * 该packet的编号，packet产生的时候就赋值 <p /> 一个报文产生器产生的编号是唯一的，但报文产生器之间不是唯一的
     */
    public long packetIndex;
    /**
     * 该packet所属的flow的编号，在switch流表匹配的时候赋值 <p />
     * 一个switch产生的编号是唯一的，但多个switch之间不是唯一的
     */
    public long flowIndex;
    /**
     * 该报文数据来源的编号，供报文产生器内部使用
     */
    public int dataSourceIndex;
    /**
     * 表项的添加时间，供flowTable使用
     */
    public long createTime;
    /**
     * 表项的过期时间，供flowTable使用
     */
    public long deadline;
    /**
     * controller处理时间，供controller使用
     */
    public double controller_delay;
    public long in_controller_time;
    public long out_controller_time;

    @Override
    public int compareTo(PacketStatistic o) {
        long ret = timestamp - o.timestamp;
        if (ret == 0) {
            return symbol(packetIndex - o.packetIndex);
        }
        return symbol(ret);
    }

    private int symbol(long ret) {
        if (ret > 0) {
            return 1;
        } else if (ret < 0) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("PacketNum:");
        ret.append(packetIndex);
        ret.append(", FlowNum:");
        ret.append(flowIndex);
        ret.append(", ");
        ret.append("Timestamp:");
        ret.append(timestamp);
        ret.append(" [");
        ret.append(Formater.foramtTime(timestamp));
        ret.append("]");
        return ret.toString();
    }
}
