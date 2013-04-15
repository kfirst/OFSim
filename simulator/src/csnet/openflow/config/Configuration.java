package csnet.openflow.config;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import csnet.openflow.component.controller.Controller;
import csnet.openflow.component.link.Link;
import csnet.openflow.component.packetGenerator.PcapGenerator;
import csnet.openflow.component.switchx.Switch;
import csnet.openflow.logger.Logger;
import csnet.openflow.util.fileOperation.FileReader;
import java.util.logging.Level;

public class Configuration {

    private static final String PCAP_GENERATOR = "pcap_generator";
    private static final String SWITCH = "switch";
    private static final String CONTROLLER = "controller";
    private static final String INTERVAL = "interval";
    private static final String LOGGER = "logger";
    private static final String LINK = "link";
    private LoggerInterpreter logger;
    private PcapGeneratorInterpreter generator;
    private SwitchInterpreter switch1;
    private ControllerInterpreter controller;
    private LinkInterpreter link;
    private long interval;

    public Configuration() {
        logger = new LoggerInterpreter();
        generator = new PcapGeneratorInterpreter();
        switch1 = new SwitchInterpreter();
        controller = new ControllerInterpreter();
        link = new LinkInterpreter();
    }

    public void load(String configFile) {
        try {
            String config = getConfigString(configFile);
            interpreter(config);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getConfigString(String configFile) throws IOException {
        FileReader reader;
        reader = new FileReader(configFile);
        StringBuilder config = new StringBuilder();
        String current = null;
        while ((current = reader.readLine()) != null) {
            config.append(current.trim());
        }
        reader.close();
        return config.toString();
    }

    private void interpreter(String config) throws JSONException {
        JSONObject json = new JSONObject(config);
        logger.interpreter(json.getJSONObject(LOGGER));
        generator.interpreter(json.getJSONObject(PCAP_GENERATOR));
        switch1.interpreter(json.getJSONObject(SWITCH));
        controller.interpreter(json.getJSONObject(CONTROLLER));
        interval = json.getLong(INTERVAL);
        if (json.has(LINK)) {
            link.interpreter(json.getJSONObject(LINK));
        }
    }

    public Logger getLogger() {
        return logger.getLogger();
    }

    public PcapGenerator getGenerator() {
        return generator.getGenerator();
    }

    public Switch getSwitch() {
        return switch1.getSwitch();
    }

    public Controller getController() {
        return controller.getController();
    }

    public Link getLink(String name) {
        return link.getLink(name);
    }

    public long getInterval() {
        return interval;
    }
}
