package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceParserApp;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/19/11
 * Time: 6:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClustersParser implements Observer, AbstractParser {

    TraceFile file;
    private Logger log;
    private long duration;
    private HashMap<String, String>[] clusters;
    private double[] avgSize;
    private XYSeries[] series;

    private String prefix;
    private String delimiter = ";";
    private int node = 1;
    private int type = 2;
    private int cluster = 3;


    public ClustersParser() {
        log = TraceParserApp.log;
        log.info("ClustersParser initialized");

    }

    public ClustersParser(TraceFile f, String template) {
        log = TraceParserApp.log;
        //log.info("EventParser initialized");
        duration = f.duration();


        file = f;
        duration = f.duration() / 1000+1;


        clusters = new HashMap[(int) duration];
        //clusters = new int[f.nodesize()][(int) duration];

        for (int i = 0; i < duration; i++) {
            clusters[i] = new HashMap<String, String>();
            clusters[i].clear();
        }
        avgSize = new double[(int) duration];

        final String[] parts = template.split(delimiter);
        prefix = parts[0];
        if (parts[1].equals("%s"))
            node = 1;
        else if (parts[1].equals("%t"))
            type = 1;
        else if (parts[1].equals("%d"))
            cluster = 1;

        if (parts[2].equals("%s"))
            node = 2;
        else if (parts[2].equals("%t"))
            type = 2;
        else if (parts[2].equals("%d"))
            cluster = 2;

        if (parts[3].equals("%s"))
            node = 3;
        else if (parts[3].equals("%t"))
            type = 3;
        else if (parts[3].equals("%d"))
            cluster = 3;


        log.info("ClustersParser initialized");
    }

    public ChartPanel getPlot(boolean has_title, boolean aggregate, String title, String xlabel, String ylabel) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] clustersSeries = null;
        if (aggregate) {
            clustersSeries = getSeries_aggregate();
        } else {
            clustersSeries = getSeries();
        }

        for (int i = 0; i < clustersSeries.length; i++) {
            dataset.addSeries(clustersSeries[i]);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xlabel,
                ylabel,
                dataset, PlotOrientation.VERTICAL, true, true, false);


        return new ChartPanel(chart);
    }

    public XYSeries[] getSeries() {

        XYSeries[] series = new XYSeries[2];
        series[0] = new XYSeries("Clusters");
        series[1] = new XYSeries("Avg. Size of Cluster");


        for (int i = 0; i < duration; i++) {
            int cluster_count = 0;
            int simple_count = 0;
            //log.info(clusters[i].keySet().size());
            for (String key : clusters[i].keySet()) {

                if (clusters[i].get(key).equals(key)) {
                    //log.info(key + " - " + clusters[i].get(key) + " N");
                    cluster_count++;
                } else {
                    //log.info(key + " - " + clusters[i].get(key));
                    simple_count++;
                }
            }


            series[0].add(i, cluster_count);
            if (cluster_count > 0) {
                //log.info("Clusters : " + cluster_count + " clSize : " + (simple_count + cluster_count) / cluster_count);
                series[1].add(i, (simple_count + cluster_count) / cluster_count);
            } else {
                //log.info("Clusters : " + cluster_count + " clSize : 0");
                series[1].add(i, 0);
            }
        }

        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public XYSeries[] getSeries_aggregate() {
        return getSeries();
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (m.text().startsWith(prefix)) {
            //log.info("Cluster@" + m.time() + ":" + m.urn());
            final String[] mess = m.text().split(delimiter);
            set_cluster(mess[node], mess[cluster], ((int) ((m.time() - file.starttime()) / 1000)));
        }
    }

    private void set_cluster(String node, String clust, int time) {
        for (int i = time; i < duration; i++) {
            clusters[i].put(node, clust);
        }
    }
}
