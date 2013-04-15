package csnet.openflow;

import csnet.openflow.config.Configuration;
import csnet.openflow.viewer.FlowModRateViewer;
import csnet.openflow.viewer.FlowSurvivalTimeViewer;
import csnet.openflow.viewer.FlowTableSizeViewer;
import csnet.openflow.viewer.InputBitRateViewer;
import csnet.openflow.viewer.PacketInBitRateViewer;
import csnet.openflow.viewer.PacketInRateViewer;
import csnet.openflow.viewer.PacketLossRateViewer;
import csnet.openflow.viewer.dataLoader.DataLoader;
import java.util.Map;
import java.util.TreeMap;

public class Viewer {

    public static void main(String[] args) {
        if (args.length < 3) {
            showUsage();
            return;
        }

        String configNameAndPath = args[0];
        String resultsToShow = args[1];
        int calculateInterval = Integer.valueOf(args[2]);
        boolean display = true;
        if (args.length > 3) {
            display = Boolean.valueOf(args[3]);
        }

        Map<String, DataLoader> loaders = getLoaders(configNameAndPath);
        System.out.println("Loading Data ...");
        for (char c : resultsToShow.toCharArray()) {
            show(c, loaders, calculateInterval, display);
        }
    }

    private static Map<String, DataLoader> getLoaders(String str) {
        TreeMap<String, DataLoader> loaders = new TreeMap<>();
        String[] pairs = str.split(",");
        for (String pair : pairs) {
            int index = pair.indexOf(':');
            String name, path;
            if (index != -1) {
                name = pair.substring(0, index);
                path = pair.substring(index + 1);
            } else {
                name = pair;
                path = pair;
            }
            System.out.println("Loading Config File [" + path + "] ...");
            Configuration c = new Configuration(path);
            loaders.put(name, c.getLoader());
        }
        return loaders;
    }

    private static void show(char toShow, Map<String, DataLoader> loaders, int interval, boolean display) {
        switch (toShow) {
            case 'f':
                new FlowModRateViewer(loaders, interval, display).start();
                break;
            case 'i':
                new InputBitRateViewer(loaders, interval, display).start();
                break;
            case 'p':
                new PacketInRateViewer(loaders, interval, display).start();
                break;
            case 'P':
                new PacketInBitRateViewer(loaders, interval, display).start();
                break;
            case 't':
                new FlowTableSizeViewer(loaders, interval, display).start();
                break;
            case 's':
                new FlowSurvivalTimeViewer(loaders, interval, display).start();
                break;
            case 'l':
                new PacketLossRateViewer(loaders, interval, display).start();
                break;
        }
    }

    private static void showUsage() {
        System.out.println("Usage: java -Xms512m -Xmx1024m -jar openflowViewer "
                + "configNameAndPath resultsToShow calculateInterval [display]");
        System.out.println("configNameAndPath: [eg. name1:./config1.ini,name2:./config1.ini]");
        System.out.println("    pairs of name and path of configuration files, name and path are separated by colon, each pair is separated by commas");
        System.out.println("resultsToShow: [eg. fip]");
        System.out.println("    f - FlowModRate");
        System.out.println("    i - InputBitRate");
        System.out.println("    p - PacketInRate");
        System.out.println("    P - PacketInBitRate");
        System.out.println("    t - FlowTableSize");
        System.out.println("    s - FlowSurvivalTime");
        System.out.println("    l - PacketLossRate");
        System.out.println("calculateInterval: [eg. 100000]");
        System.out.println("    time interval bewteen adjacent point in microsecond");
        System.out.println("display: [eg. false]");
        System.out.println("    whether show the chart, default true");
    }
}
