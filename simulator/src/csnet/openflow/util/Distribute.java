package csnet.openflow.util;

import java.util.Random;

/**
 *
 * @author kfirst
 */
public class Distribute {

    private static Random random = new Random(System.currentTimeMillis());

    public static double exponential(double average) {
        return -1 * average * Math.log1p(-random.nextDouble());
    }
}
