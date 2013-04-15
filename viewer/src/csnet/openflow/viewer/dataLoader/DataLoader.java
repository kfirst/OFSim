package csnet.openflow.viewer.dataLoader;

public interface DataLoader {

    public void close();

    public long getStartTime();

    public long getEndTime();

    public int getTotalSizeOfInputPacket(long startTime, long endTime);
    
    public int getTotalHeaderSizeOfPacketIn(long startTime, long endTime);

    public int getTotalNumOfPacketIn(long startTime, long endTime);

    public int getTotalNumOfFlowMod(long startTime, long endTime);
    
    public int getFlowTableSize(long time);
    
    public int getMaxFlowSurvivalTime();
    
    public int getTotalNumOfFlowSurvivalTimeBetween(long startTime, long endTime);
    
    public int getTotalNumOfPacket(long startTime, long endTime);
    
    public int getTotalNumOfLostPacket(long startTime, long endTime);
}
