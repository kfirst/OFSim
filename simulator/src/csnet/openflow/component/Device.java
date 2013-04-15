package csnet.openflow.component;

public interface Device {

    /**
     * 按时间调度，device需将该时间之前发生的所有事件处理完毕
     *
     * @param timestamp 时间
     * @return 是否需要继续调度
     */
    public boolean schedule(long timestamp);
}
