package csnet.openflow.viewer;

import csnet.openflow.viewer.dataLoader.DataLoader;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFrame;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author kfirst
 */
public abstract class BaseViewer extends Thread {

    private Map<String, DataLoader> loader;
    private boolean display;

    public BaseViewer(Map<String, DataLoader> loaders, boolean display) {
        this.loader = loaders;
        this.display = display;
    }

    @Override
    public void run() {
        loadAllData();
        if (display) {
            show(getChart());
        }
    }

    protected abstract String getViewerName();

    protected abstract JFreeChart getChart();

    protected abstract void loadData(String name, DataLoader loader);

    private void loadAllData() {
        for (Iterator<Map.Entry<String, DataLoader>> it = loader.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, DataLoader> entry = it.next();
            loadData(entry.getKey(), entry.getValue());
        }
    }

    private void show(JFreeChart chart) {
        ChartFrame frame = new ChartFrame(getViewerName(), chart);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
