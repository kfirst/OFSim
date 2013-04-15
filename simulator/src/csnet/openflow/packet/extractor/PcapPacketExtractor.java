package csnet.openflow.packet.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;

import csnet.openflow.packet.model.Packet;

/**
 * 从Pcap文件中读取报文，并按照文件中的报文顺序输出
 * <p>
 * 若添加多个Pcap文件，本类将按照时间对多个文件进行归并， 若每个文件中的报文都是按时间从早到晚排好序的，
 * 则最终的输出将是按时间从早到晚的排好序的所有报文。
 * 
 * @author kfirst
 * 
 */
public class PcapPacketExtractor {
    private List<Pcap> pcaps;
    private PriorityQueue<Packet> packetSorter;

    private PcapPacket pcapPacket;
    private PacketConverter converter;

    private long totalNum;
    private long convertNum;
    private long errorNum;

    public PcapPacketExtractor() {
        pcaps = new ArrayList<Pcap>();
        packetSorter = new PriorityQueue<Packet>();

        pcapPacket = new PcapPacket(JMemory.POINTER);
        converter = new PacketConverter();
    }

    /**
     * 添加一个目录或文件
     * 
     * @param pcapPath
     *            目录的路径
     */
    public void add(String pcapPath) {
        File dir = new File(pcapPath);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles())
                if (file.isFile())
                    addFile(file);
        } else if (dir.isFile()) {
            addFile(dir);
        }
    }

    private void addFile(File pcapFile) {
        Pcap pcap = getPcapFromFile(pcapFile);
        pcaps.add(pcap);
        Packet packet = getNextPacketFromPcap(pcap);
        if (packet != null) {
            packet.statistic.dataSourceIndex = pcaps.size() - 1;
            packetSorter.add(packet);
        }
    }

    private Pcap getPcapFromFile(File pcapFile) {
        if (!pcapFile.exists())
            throw new IllegalArgumentException("File["
                    + pcapFile.getAbsolutePath() + "] doesn`t exist!");
        if (!pcapFile.isFile())
            throw new IllegalArgumentException("File ["
                    + pcapFile.getAbsolutePath() + "] is not a legal file!");
        StringBuilder error = new StringBuilder();
        Pcap pcap = Pcap.openOffline(pcapFile.getAbsolutePath(), error);
        if (pcap == null)
            throw new IllegalArgumentException("open file["
                    + pcapFile.getAbsolutePath() + "] error! "
                    + error.toString());
        return pcap;
    }

    /**
     * 弹出下一个报文
     * 
     * @return
     */
    public Packet pollPacket() {
        Packet earliest = packetSorter.poll();
        if (earliest != null) {
            int source = earliest.statistic.dataSourceIndex;
            Packet packet = getNextPacketFromPcap(pcaps.get(source));
            if (packet != null) {
                packet.statistic.dataSourceIndex = source;
                packetSorter.add(packet);
            }
        }
        return earliest;
    }

    /**
     * 取出下一个报文，但不弹出（即可以再次取到同一个报文）
     * 
     * @return
     */
    public Packet peekPacket() {
        return packetSorter.peek();
    }

    public boolean isEmpty() {
        return packetSorter.isEmpty();
    }

    private Packet getNextPacketFromPcap(Pcap pcap) {
        while (true) {
            int ret = pcap.nextEx(pcapPacket);
            if (ret == Pcap.NEXT_EX_OK) {
                try {
                    Packet packet = converter.convert(pcapPacket);
                    ++totalNum;
                    if (packet != null) {
                        ++convertNum;
                        return packet;
                    }
                } catch (Exception e) {
                    ++errorNum;
                }
            } else if (ret == Pcap.NEXT_EX_EOF)
                return null;
        }
    }

    public long getTotalNum() {
        return totalNum;
    }

    public long getConvertNum() {
        return convertNum;
    }

    public long getErrorNum() {
        return errorNum;
    }

    public static void main(String[] args) {
        PcapPacketExtractor generator = new PcapPacketExtractor();
        generator
                .add("D:/Kfirst/Project/Openflow/openflow-simulator-old/simulator/chicoga/data");
        for (int i = 0; i < 50; i++) {
            Packet packet = generator.pollPacket();
            System.out.println(packet);
            System.out.println();
        }
    }
}
