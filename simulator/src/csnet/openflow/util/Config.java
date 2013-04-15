package csnet.openflow.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Map.Entry;

import csnet.openflow.util.fileOperation.FileReader;
import csnet.openflow.util.fileOperation.FileWriter;

public class Config {
    private String fileName;
    private Map<String, String> values;

    public Config(String fileName) throws IOException {
        this.fileName = fileName;
        values = new HashMap<String, String>();
        initialize();
    }

    public String get(String key) {
        return values.get(key);
    }

    public String set(String key, String value) {
        return values.put(key, value);
    }

    public void save() throws IOException {
        FileWriter wf = new FileWriter(fileName, false, Constants.UTF_8);
        for (Entry<String, String> pair : values.entrySet())
            wf.writeLine(pair.getKey() + "=" + pair.getValue());
        wf.close();
    }

    private void initialize() throws IOException {
        FileReader rf = new FileReader(fileName, Constants.UTF_8);
        String line = null;
        while ((line = rf.readLine()) != null) {
            String[] pair = line.split("=");
            if (pair.length != 2)
                throw new InputMismatchException("Format of config file ["
                        + fileName + "] is error!");
            values.put(pair[0].trim(), pair[2].trim());
        }
        rf.close();
    }
}