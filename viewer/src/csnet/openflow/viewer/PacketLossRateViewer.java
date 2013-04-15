package csnet.openflow.viewer;

import csnet.openflow.util.Constants;
import csnet.openflow.util.Formater;
import csnet.openflow.viewer.dataLoader.DataLoader;
import java.util.Date;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class PacketLossRateViewer extends BaseViewer {

    private static final String NAME = "Packet Loss Rate";
    private int interval;
    private TimeSeriesCollection collection;

    public PacketLossRateViewer(Map<String, DataLoader> loader, int interval, boolean display) {
        super(loader, display);
        this.interval = interval;
        this.collection = new TimeSeriesCollection();
    }

    @Override
    protected void loadData(String name, DataLoader loader) {
        long start = loader.getStartTime();
        long current = start;
        long end = loader.getEndTime();
        double totalSize = 0, totalLose = 0, average = 0;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;

        TimeSeries series = new TimeSeries(name);
        while (current + interval < end) {
            Millisecond millisecond = new Millisecond(new Date(current / 1000));
            double size = loader.getTotalNumOfPacket(current, current + interval);
            double loss = loader.getTotalNumOfLostPacket(current, current + interval);
            series.add(millisecond, loss * 100 / size);
            current += interval;
            if (current - start >= Constants.AVERAGE_START_TIME) {
                totalLose += loss;
                totalSize += size;
            }
            double count = loss / size;
            if (count > max) {
                max = count;
            }
            if (count < min) {
                min = count;
            }
        }
        collection.addSeries(series);

        if (totalSize > 0) {
            average = totalLose / totalSize;
        }
        System.out.println("[" + NAME + ", " + name + "] loaded."
                + " Average: " + Formater.formatPercentage(average)
                + " Max: " + Formater.formatDouble(max)
                + " Min: " + Formater.formatDouble(min));
    }

    @Override
    protected String getViewerName() {
        return NAME;
    }

    @Override
    protected JFreeChart getChart() {
        return ChartFactory.createTimeSeriesChart(
                NAME,
                "time",
                "%",
                collection,
                true, true, true);
    }
}
