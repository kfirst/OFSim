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

public class InputBitRateViewer extends BaseViewer {

    private static final String NAME = "Input Bit Rate";
    private int interval;
    private double multiple;
    private TimeSeriesCollection collection;

    public InputBitRateViewer(Map<String, DataLoader> loaders, int interval, boolean display) {
        super(loaders, display);
        this.interval = interval;
        this.multiple = 8000000.0 / interval;
        this.collection = new TimeSeriesCollection();
    }

    @Override
    protected void loadData(String name, DataLoader loader) {
        long start = loader.getStartTime();
        long current = start;
        long end = loader.getEndTime();
        int circle = 0;
        double average = 0;

        TimeSeries series = new TimeSeries(name);
        while (current + interval < end) {
            Millisecond millisecond = new Millisecond(new Date(current / 1000));
            double size = loader.getTotalSizeOfInputPacket(current, current + interval) * multiple;
            series.add(millisecond, size);
            current += interval;
            if (current - start >= Constants.AVERAGE_START_TIME) {
                circle++;
                average += size;
            }
        }
        collection.addSeries(series);

        if (circle > 0) {
            average /= circle;
        }
        System.out.println("[" + NAME + ", " + name + "] loaded."
                + " Average: " + Formater.formatDouble(average) + " b/s, "
                + Formater.formatDouble(average / 8) + " B/s");
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
                "bit/s",
                collection,
                true, true, true);
    }
}
