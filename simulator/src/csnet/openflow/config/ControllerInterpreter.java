package csnet.openflow.config;

import org.json.JSONException;
import org.json.JSONObject;

import csnet.openflow.component.controller.ExponentialLatencyController;
import csnet.openflow.component.controller.Controller;

public class ControllerInterpreter {

    private static final String BUFFER_SIZE = "buffer_size";
    private static final String AVERAGE_LATENCY = "average_latency";
    private ExponentialLatencyController controller;

    public void interpreter(JSONObject json) throws JSONException {
        if (controller == null) {
            controller = new ExponentialLatencyController();
        }
        int bufferSize = json.getInt(BUFFER_SIZE);
        int latency = json.getInt(AVERAGE_LATENCY);
        controller.config(bufferSize, latency);
    }

    public Controller getController() {
        return controller;
    }
}
