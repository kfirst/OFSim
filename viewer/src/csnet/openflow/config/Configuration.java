package csnet.openflow.config;

import csnet.openflow.util.fileOperation.FileReader;
import csnet.openflow.viewer.dataLoader.DataLoader;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public class Configuration {

    private static final String LOGGER = "logger";
    private DataLoader loader;

    public Configuration(String configFile) {
        FileReader reader;
        try {
            reader = new FileReader(configFile);
            StringBuilder config = new StringBuilder();
            String current = null;
            while ((current = reader.readLine()) != null) {
                config.append(current.trim());
            }
            reader.close();
            load(config.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataLoader getLoader() {
        return loader;
    }

    private void load(String config) {
        try {
            JSONObject json = new JSONObject(config);
            loader = LoaderInterpreter.interpreter(
                    json.getJSONObject(LOGGER));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
