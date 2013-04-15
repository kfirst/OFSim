package csnet.openflow.config;

import csnet.openflow.component.interfacex.BandLimitedDropInterface;
import csnet.openflow.component.interfacex.BandLimitedPostponeInterface;
import csnet.openflow.component.interfacex.Interface;
import csnet.openflow.component.switchx.Switch;
import csnet.openflow.component.switchx.flowEntryDelay.FlowEntryDelay;
import csnet.openflow.component.switchx.flowEntryDelay.LinearFlowEntryDelay;
import csnet.openflow.component.switchx.flowTable.FlowTable;
import csnet.openflow.component.switchx.flowTable.LRUFlowTable;
import csnet.openflow.component.switchx.packetBuffer.PacketBuffer;
import csnet.openflow.packet.comparator.Ipv4Comparator;
import csnet.openflow.packet.comparator.PacketComparator;
import csnet.openflow.packet.comparator.TcpComparator;
import csnet.openflow.packet.comparator.UdpComparator;
import csnet.openflow.packet.model.Packet;
import csnet.openflow.packet.model.Packet.SizeType;
import csnet.openflow.packet.model.networkLayer.Ipv4Header;
import csnet.openflow.packet.model.transportLayer.TCPHeader;
import csnet.openflow.packet.model.transportLayer.UDPHeader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class SwitchInterpreter {

    private static final String IN_INTERFACE = "in_interface";
    private static final String OUT_INTERFACE = "out_interface";
    private static final String TO_CONTROLLER_INTERFACE = "to_controller_interface";
    private static final String FLOW_ENTRY_DELAY = "flow_entry_delay";
    private static final String FLOW_TABLE = "flow_table";
    private static final String PACKET_BUFFER = "packet_buffer";
    private static final String BANDWIDTH = "bandwidth";
    private static final String TYPE = "type";
    private static final String DROP = "drop";
    private static final String POSTPONE = "postpone";
    private static final String BUFFER_SIZE = "buffer_size";
    private static final String SIZE_TYPE = "size_type";
    private static final String LINEAR = "linear";
    private static final String A = "a";
    private static final String B = "b";
    private static final String LRU = "lru";
    private static final String BUFFER_TIME = "buffer_time";
    private static final String LATENCY = "latency";
    private static final String SIZE = "size";
    private static final String COMPARATOR = "comparator";
    private static final String IPV4 = "ipv4";
    private static final String UDP = "udp";
    private static final String TCP = "tcp";
    private Switch switch1;
    private FlowTable flowTable;
    private Interface inInterface;
    private Interface outInterface;
    private Interface toControllerInterface;
    private FlowEntryDelay flowEntryDelay;
    private PacketBuffer packetBuffer;
    private Comparator<Packet> comparator;

    public void interpreter(JSONObject json)
            throws JSONException {
        getFlowTable(json.getJSONObject(FLOW_TABLE));
        inInterface = getInterface(inInterface, json.getJSONObject(IN_INTERFACE));
        outInterface = getInterface(outInterface, json.getJSONObject(OUT_INTERFACE));
        toControllerInterface = getInterface(toControllerInterface, json.getJSONObject(TO_CONTROLLER_INTERFACE));
        getFlowEntryDelay(json.getJSONObject(FLOW_ENTRY_DELAY));
        getPacketBuffer(json.getJSONObject(PACKET_BUFFER));
        if (switch1 == null) {
            switch1 = new Switch();
            switch1.setFlowTable(flowTable);
            switch1.setInInterface(inInterface);
            switch1.setOutInterface(outInterface);
            switch1.setToControllerInterface(toControllerInterface);
            switch1.setFlowEntryDelay(flowEntryDelay);
            switch1.setPacketBuffer(packetBuffer);
        }
    }

    public Switch getSwitch() {
        return switch1;
    }

    private void getFlowEntryDelay(JSONObject json)
            throws JSONException {
        String type = json.getString(TYPE);
        int bufferSize;
        int a, b;
        switch (type) {
            case LINEAR:
                if (flowEntryDelay == null) {
                    flowEntryDelay = new LinearFlowEntryDelay(flowTable);
                }
                bufferSize = json.getInt(BUFFER_SIZE);
                a = json.getInt(A);
                b = json.getInt(B);
                ((LinearFlowEntryDelay) flowEntryDelay).config(bufferSize, a, b);
                break;
        }
    }

    private Interface getInterface(Interface interface1, JSONObject json)
            throws JSONException {
        String type = json.getString(TYPE);
        long bandwidth;
        int bufferSize;
        SizeType sizeType;
        switch (type) {
            case DROP:
                if (interface1 == null) {
                    interface1 = new BandLimitedDropInterface();
                }
                bandwidth = json.getLong(BANDWIDTH);
                sizeType = SizeType.valueOf(json.getString(SIZE_TYPE));
                ((BandLimitedDropInterface) interface1).config(bandwidth, sizeType);
                break;
            case POSTPONE:
                if (interface1 == null) {
                    interface1 = new BandLimitedPostponeInterface();
                }
                bandwidth = json.getLong(BANDWIDTH);
                bufferSize = json.getInt(BUFFER_SIZE);
                sizeType = SizeType.valueOf(json.getString(SIZE_TYPE));
                ((BandLimitedPostponeInterface) interface1).config(bandwidth, bufferSize, sizeType);
                break;
        }
        return interface1;
    }

    private void getFlowTable(JSONObject json) throws JSONException {
        String type = json.getString(TYPE);
        long bufferTime;
        long delay;
        int size;
        switch (type) {
            case LRU:
                if (flowTable == null) {
                    comparator = getComparator(json.getJSONObject(COMPARATOR));
                    flowTable = new LRUFlowTable(comparator);
                }
                bufferTime = json.getLong(BUFFER_TIME);
                delay = json.getLong(LATENCY);
                size = json.getInt(SIZE);
                ((LRUFlowTable) flowTable).config(bufferTime, delay, size);
                break;
        }
    }

    private void getPacketBuffer(JSONObject json) throws JSONException {
        if (packetBuffer == null) {
            packetBuffer = new PacketBuffer(comparator);
        }
        int size = json.getInt(SIZE);
        packetBuffer.config(size);
    }

    private Comparator<Packet> getComparator(JSONObject json)
            throws JSONException {
        PacketComparator comparator = new PacketComparator();
        Iterator<String> iterator = json.keys();
        while (iterator.hasNext()) {
            String name = iterator.next();
            JSONObject fields = json.getJSONObject(name);
            switch (name) {
                case IPV4:
                    Ipv4Header ipv4Header = new Ipv4Header();
                    List<Ipv4Header.Field> ipv4Fields = new ArrayList<Ipv4Header.Field>();
                    for (Object key : fields.keySet()) {
                        Ipv4Header.Field field = Ipv4Header.Field
                                .valueOf((String) key);
                        ipv4Header.set(field, fields.getInt((String) key));
                        ipv4Fields.add(field);
                    }
                    comparator.addNetwordComparator(Ipv4Header.ID,
                            new Ipv4Comparator(ipv4Fields, ipv4Header));
                    break;
                case UDP:
                    UDPHeader udpHeader = new UDPHeader();
                    List<UDPHeader.Field> udpFields = new ArrayList<UDPHeader.Field>();
                    for (Object key : fields.keySet()) {
                        UDPHeader.Field field = UDPHeader.Field
                                .valueOf((String) key);
                        udpHeader.set(field, fields.getInt((String) key));
                        udpFields.add(field);
                    }
                    comparator.addTransportComparator(UDPHeader.ID,
                            new UdpComparator(udpFields, udpHeader));
                    break;
                case TCP:
                    TCPHeader tcpHeader = new TCPHeader();
                    List<TCPHeader.Field> tcpFields = new ArrayList<TCPHeader.Field>();
                    for (Object key : fields.keySet()) {
                        TCPHeader.Field field = TCPHeader.Field
                                .valueOf((String) key);
                        tcpHeader.set(field, fields.getInt((String) key));
                        tcpFields.add(field);
                    }
                    comparator.addTransportComparator(TCPHeader.ID,
                            new TcpComparator(tcpFields, tcpHeader));
                    break;
            }
        }
        return comparator;
    }
}
