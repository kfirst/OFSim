package csnet.openflow.config;

import csnet.openflow.viewer.dataLoader.DataLoader;
import csnet.openflow.viewer.dataLoader.DatabaseLoader;
import org.json.JSONException;
import org.json.JSONObject;

public class LoaderInterpreter {

    private static final String TYPE = "type";
    private static final String DATABASE = "database";
    private static final String TABLE = "table";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    public static DataLoader interpreter(JSONObject json) throws JSONException {
        String type = json.getString(TYPE);
        String table, user, password;
        switch (type) {
            case DATABASE:
                table = json.getString(TABLE);
                user = json.getString(USER);
                password = json.getString(PASSWORD);
                return new DatabaseLoader(table, user, password);
            default:
                break;
        }
        return null;
    }
}
