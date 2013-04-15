package csnet.openflow.viewer;

import csnet.openflow.viewer.dataLoader.DataLoader;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author kfirst
 */
public class FlowSurvivalTimeViewer extends BaseViewer {

    private static final String NAME = "Flow Survival Time";
    private int interval;
    private XYSeriesCollection collection;

    public FlowSurvivalTimeViewer(Map<String, DataLoader> loaders, int interval, boolean display) {
        super(loaders, display);
        this.interval = interval;
        collection = new XYSeriesCollection();
    }

    @Override
    protected void loadData(String name, DataLoader loader) {
        long start = 0;
        long current = start;
        long end = loader.getMaxFlowSurvivalTime();
        int sum = 0;
        int total = loader.getTotalNumOfFlowMod(0, Long.MAX_VALUE);

        XYSeries series = new XYSeries(name);
        do {
            current += interval;
            sum += loader.getTotalNumOfFlowSurvivalTimeBetween(current - interval, current);
            double count = sum / (double) total;
            if (count > 0) {
                series.add(current / 1000000.0, count);
            }
        } while (current < end);
        collection.addSeries(series);

        System.out.println("[" + NAME + ", " + name + "] loaded."
                + " Total flow: " + total);
    }

    @Override
    protected String getViewerName() {
        return NAME;
    }

    @Override
    protected JFreeChart getChart() {
        return ChartFactory.createXYLineChart(
                NAME,
                "time",
                "probability",
                collection,
                PlotOrientation.VERTICAL,
                true, true, true);
    }
}
