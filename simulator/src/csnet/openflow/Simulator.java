package csnet.openflow;

import csnet.openflow.component.controller.Controller;
import csnet.openflow.component.link.ConstantLatencyLink;
import csnet.openflow.component.link.Link;
import csnet.openflow.component.packetGenerator.PcapGenerator;
import csnet.openflow.component.receiver.Receiver;
import csnet.openflow.component.switchx.Switch;
import csnet.openflow.config.Configuration;
import csnet.openflow.logger.Logger;
import java.util.ArrayList;
import java.util.List;

public class Simulator {

    private Logger logger;
    private PcapGenerator generator;
    private Switch switch1;
    private Controller controller;
    private Receiver receiver;
    private long interval;
    private int currentConfig;
    private Configuration config;
    private List<String> configPathes;
    private List<Long> configEndTimes;

    public Simulator(List<String> configPathes, List<Long> configEndTimes) {
        this.configPathes = configPathes;
        this.configEndTimes = configEndTimes;
        loadConfig();
    }

    public void run() {
        long start = System.currentTimeMillis();
        long base = 1000000 / interval;
        long begin = generator.getStartTime();
        long time = begin + interval;
        int num = 0;
        long currentConfigEndTime = configEndTimes.get(currentConfig);
        while (true) {
            if (num % base == 0) {
                System.out.println(time + ", circle " + num + ", "
                        + "time " + (System.currentTimeMillis() - start) / 1000.0 + "s");
            }
            num++;

            if (currentConfigEndTime <= time - begin) {
                reloadConfig();
                if (currentConfig < configPathes.size()) {
                    currentConfigEndTime = configEndTimes.get(currentConfig);
                } else {
                    currentConfigEndTime = Long.MAX_VALUE;
                }
            }

            boolean flag = generator.schedule(time);
            switch1.schedule(time);
            flag |= receiver.schedule(time);
            time += interval;
            if (!flag) {
                break;
            }
        }
        logger.close();
    }

    private void loadConfig() {
        String configPath = configPathes.get(currentConfig);
        System.out.println("Loading Config File [" + configPath + "] ...");
        config = new Configuration();
        config.load(configPath);

        interval = config.getInterval();
        logger = config.getLogger();

        generator = config.getGenerator();
        generator.setLogger(logger);

        controller = config.getController();
        controller.setLogger(logger);

        switch1 = config.getSwitch();
        switch1.setLogger(logger);
        switch1.setController(controller);

        receiver = new Receiver();
        receiver.setLogger(logger);

        Link g2s = new ConstantLatencyLink(0);
        Link s2r = new ConstantLatencyLink(0);
        Link s2c = config.getLink("s2c");
        Link c2s = config.getLink("c2s");

        generator.connect(g2s);
        switch1.connect(g2s, s2r, s2c, c2s);
        controller.connect(s2c, c2s);
        receiver.connect(s2r);
    }

    private void reloadConfig() {
        currentConfig++;
        if (currentConfig >= configPathes.size()) {
            generator.stop();
            return;
        }
        String configPath = configPathes.get(currentConfig);
        System.out.println("Loading Config File [" + configPath + "] ...");
        config.load(configPath);

        interval = config.getInterval();
    }

    public static void main(String[] args) {
        List<String> configPathes = new ArrayList<>();
        List<Long> configEndTimes = new ArrayList<>();
        if (args.length > 0) {
            for (int i = 0; i < args.length; i += 2) {
                configPathes.add(args[i]);
                if (i + 1 < args.length) {
                    configEndTimes.add(Long.valueOf(args[i + 1]));
                } else {
                    configEndTimes.add(Long.MAX_VALUE);
                }
            }
        } else {
            configPathes.add("./config.ini");
            configEndTimes.add(Long.MAX_VALUE);
        }

        System.out.println("Constructing Simulator ...");
        Simulator simulator = new Simulator(configPathes, configEndTimes);

        System.out.println("Running ...");
        simulator.run();
        System.out.println("Finish.");
    }
}
