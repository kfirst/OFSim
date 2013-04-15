package csnet.openflow.config;

import csnet.openflow.component.packetGenerator.PcapGenerator;
import csnet.openflow.packet.filter.HeaderFilter;
import csnet.openflow.packet.filter.PacketFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PcapGeneratorInterpreter {

    private final static String FILTER = "filter";
    private final static String PCAP_PATH = "pcap_path";
    private final static String NETWORK = "network";
    private final static String TRANSPORT = "transport";
    private PcapGenerator generator;

    public void interpreter(JSONObject json)
            throws JSONException {
        if (generator == null) {
            generator = new PcapGenerator();
            JSONArray pathes = json.getJSONArray(PCAP_PATH);
            for (int i = pathes.length(); i > 0; --i) {
                generator.addPcap(pathes.getString(i - 1));
            }
        }
        PacketFilter filter = getFilter(json.getJSONObject(FILTER));
        generator.config(filter);
    }

    public PcapGenerator getGenerator() {
        return generator;
    }

    private PacketFilter getFilter(JSONObject json) throws JSONException {
        HeaderFilter filter = new HeaderFilter();
        JSONArray networkIds = json.getJSONArray(NETWORK);
        for (int i = networkIds.length(); i > 0; --i) {
            filter.addPermitedNetworkId(networkIds.getString(i - 1));
        }
        JSONArray transportIds = json.getJSONArray(TRANSPORT);
        for (int i = transportIds.length(); i > 0; --i) {
            filter.addPermitedTransportId(transportIds.getString(i - 1));
        }
        return filter;
    }
}
