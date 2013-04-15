package csnet.openflow.viewer;

import csnet.openflow.util.Constants;
import csnet.openflow.util.Formater;
import csnet.openflow.viewer.dataLoader.DataLoader;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class FlowModRateViewer extends BaseViewer {

    private static final String NAME = "Flow-mod Rate";
    private int interval;
    private double multiple;
    private TimeSeriesCollection collection;

    public FlowModRateViewer(Map<String, DataLoader> loaders, int interval, boolean display) {
        super(loaders, display);
        this.interval = interval;
        this.multiple = 1000000.0 / interval;
        this.collection = new TimeSeriesCollection(TimeZone.getTimeZone("GMT-8:00"));
    }

    @Override
    protected void loadData(String name, DataLoader loader) {
        long start = loader.getStartTime();
        long current = start;
        long end = loader.getEndTime();
        int circle = 0;
        double average = 0;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;

        TimeSeries series = new TimeSeries(name);
        while (current + interval < end) {
            Millisecond millisecond = new Millisecond(new Date(current / 1000));
            double count = loader.getTotalNumOfFlowMod(current, current + interval) * multiple;
            series.add(millisecond, count);
            current += interval;
            if (current - start >= Constants.AVERAGE_START_TIME) {
                circle++;
                average += count;
                if (count > max) {
                    max = count;
                }
                if (count < min) {
                    min = count;
                }
            }
        }
        collection.addSeries(series);
        if (circle > 0) {
            average /= circle;
        }
        System.out.println("[" + NAME + ", " + name + "] loaded."
                + " Average: " + Formater.formatDouble(average)
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
                "num/s",
                collection,
                true, true, true);
    }
}
