package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/19/11
 * Time: 6:39 PM
 */
public class NeighborhoodParser extends AbstractParser implements Observer {

    private TraceFile file;
    private static final Logger log = Logger.getLogger(NeighborhoodParser.class);
    private HashMap<String, Integer> neighbors;


    private XYSeries[] series;

    private String prefix;


    public NeighborhoodParser() {
        prefix = "NB";
        init();

    }


    public void setTemplate(String template) {
        String delimiter = ";";
        prefix = template.substring(0, template.indexOf(delimiter));

    }


    @Override
    public String getTemplate() {
        return prefix + ";";
    }

    public NeighborhoodParser(String template) {
        setTemplate(template);
        init();
    }

    public void setFile(TraceFile file) {
        this.file = file;
    }

    public NeighborhoodParser(TraceFile f, String template) {
        //log.info("EventParser initialized");
        long duration = f.duration();


        file = f;

        String delimiter = ";";
        prefix = template.substring(0, template.indexOf(delimiter));
    }


    public void init() {
        neighbors = new HashMap<String, Integer>();
        series = new XYSeries[3];
        series[0] = new XYSeries("Avg Neighbors");
        series[1] = new XYSeries("Min Neighbors");
        series[2] = new XYSeries("Max Neighbors");
        log.info("NeighborhoodParser initialized");
    }

    public ChartPanel getPlot() {
        return getPlot(false, true, "", "", "");
    }

    public ChartPanel getPlot(boolean has_title, boolean aggregate, String title, String xlabel, String ylabel) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] clustersSeries;
        if (aggregate) {
            clustersSeries = getSeries_aggregate();
        } else {
            clustersSeries = getSeries();
        }

        for (XYSeries clustersSery : clustersSeries) {
            dataset.addSeries(clustersSery);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xlabel,
                ylabel,
                dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.white);

        return new ChartPanel(chart);
    }

    public XYSeries[] getSeries() {


        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public XYSeries[] getSeries_aggregate() {
        return getSeries();
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (m.text().startsWith(prefix)) {
            //log.info("Neighbor@" + m.time() + ":" + m.urn());
            int nb_change = 0;
//            if ((m.text().contains(prefix + ";")) | (m.text().contains(prefix + "B;"))) {
            if ((m.text().contains(prefix + "B;"))) {
                //add neighbor
                nb_change = +1;
            } else if ((m.text().contains(prefix + "D;"))) {
                nb_change = -1;
            }
            if (neighbors.containsKey(m.urn())) {
                neighbors.put(m.urn(), neighbors.get(m.urn()) + nb_change);
            } else {
                neighbors.put(m.urn(), 1);
            }
            //log.info(m.text()+ ":: "+nb_change);
            //log.info(get_avg_neighbors());
            series[0].addOrUpdate(((int) ((m.time() - file.starttime()) / 1000)), get_avg_neighbors());
            series[1].addOrUpdate(((int) ((m.time() - file.starttime()) / 1000)), get_min_neighbors());
            series[2].addOrUpdate(((int) ((m.time() - file.starttime()) / 1000)), get_max_neighbors());
        }
    }

    private double get_avg_neighbors() {
        int count = 0;
        int sum = 0;
        for (int val : neighbors.values()) {
            sum += val;
            count++;
        }
        if (count > 0)
            return (double) sum / count;
        else {
            return 0;
        }
    }

    private double get_max_neighbors() {
        int max = 0;
        for (int val : neighbors.values()) {
            if (max < val) {
                max = val;
            }
        }
        return max;
    }

    private double get_min_neighbors() {
        int min = 10000;
        for (int val : neighbors.values()) {
            if (min > val) {
                min = val;
            }
        }
        return min;
    }
}

