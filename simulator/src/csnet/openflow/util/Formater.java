package csnet.openflow.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formater {
    private static final DateFormat dateFormater = new SimpleDateFormat(
            "yyyy.MM.dd/HH:mm:ss.SSS");
    private static final NumberFormat numberFormater = new DecimalFormat("000");

    public static String foramtTime(long timestampInMicrosecond) {
        return dateFormater.format(new Date(timestampInMicrosecond / 1000))
                + numberFormater.format(timestampInMicrosecond % 1000);
    }
}
