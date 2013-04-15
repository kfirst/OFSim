package csnet.openflow.config;

import csnet.openflow.component.link.ConstantLatencyLink;
import csnet.openflow.component.link.Link;
import java.util.TreeMap;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author kfirst
 */
public class LinkInterpreter {

    private static final String LATENCY = "latency";
    private TreeMap<String, Link> links = new TreeMap<>();

    public void interpreter(JSONObject json) throws JSONException {
        for (Object key : json.keySet()) {
            links.put((String) key, createLink(json.getJSONObject((String) key)));
        }
    }

    private Link createLink(JSONObject json) throws JSONException {
        int latency = json.getInt(LATENCY);
        return new ConstantLatencyLink(latency);
    }

    public Link getLink(String name) {
        Link link = links.get(name);
        if (link == null) {
            link = new ConstantLatencyLink(0);
            links.put(name, link);
        }
        return link;
    }
}
