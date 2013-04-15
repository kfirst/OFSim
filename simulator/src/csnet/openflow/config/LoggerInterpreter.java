package csnet.openflow.config;

import org.json.JSONException;
import org.json.JSONObject;

import csnet.openflow.logger.DatabaseLogger;
import csnet.openflow.logger.Logger;

public class LoggerInterpreter {

    private static final String TYPE = "type";
    private static final String DATABASE = "database";
    private static final String TABLE = "table";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private Logger logger;

    public void interpreter(JSONObject json) throws JSONException {
        String type = json.getString(TYPE);
        String table, user, password;
        switch (type) {
            case DATABASE:
                if (logger == null) {
                    user = json.getString(USER);
                    password = json.getString(PASSWORD);
                    logger = new DatabaseLogger(user, password);
                }
                table = json.getString(TABLE);
                ((DatabaseLogger) logger).config(table);
                break;
            default:
                break;
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
