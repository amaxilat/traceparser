package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.ChartFormater;
import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceReader;
import eu.amaxilatis.java.traceparser.panels.couplePanel;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class SensorAggregationParser extends AbstractParser implements Observer, ActionListener {

    private TraceFile file;
    private static final Logger log = Logger.getLogger(SensorAggregationParser.class);

    private String delimiter = ":";
    private final JButton plotbutton;
    private final JButton updatebutton;
    private final JTextField delimitertextfield;
    public static final String Name = "SensorAggregation Parser";

    private final String sensorPrefix = "Sread";
    private final String aggregatedPrefix = "Sval";
    private final int startuptime = 100;


    private java.util.List<SensorReading> sensorReadings = new ArrayList<SensorReading>();
    private java.util.List<AggregatedSensorReading> aggregatedReadings = new ArrayList<AggregatedSensorReading>();
    private Hashtable<String, Integer> sensors = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> sensorClusters = new Hashtable<String, Integer>();
    private final JPanel rightmainpanel;


    public SensorAggregationParser() {

        init();
        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel(Name), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        plotbutton = new JButton("plot");
        plotbutton.addActionListener(this);
        updatebutton = new JButton("reload configuration");
        updatebutton.addActionListener(this);

        delimitertextfield = new JTextField(delimiter);
        JTextField meantextfield = new JTextField(sensorPrefix);
        JTextField aggregatedtextfield = new JTextField(aggregatedPrefix);
        JTextField startUptextfield = new JTextField("" + startuptime);

        leftmainpanel.add(new couplePanel(new JLabel("delimiter"), delimitertextfield));
        leftmainpanel.add(new couplePanel(new JLabel("Mean Value"), meantextfield));
        leftmainpanel.add(new couplePanel(new JLabel("Aggregated Value"), aggregatedtextfield));
        leftmainpanel.add(new couplePanel(new JLabel("Startup Time"), startUptextfield));

        JPanel plotbuttonpanel = new JPanel(new FlowLayout());
        plotbuttonpanel.add(plotbutton);
        Dimension d = new Dimension(100, 50);
        plotbuttonpanel.setPreferredSize(d);
        plotbuttonpanel.setMinimumSize(d);
        plotbuttonpanel.setMaximumSize(d);

        JPanel updatebuttonpanel = new JPanel(new FlowLayout());
        updatebuttonpanel.add(updatebutton);
        updatebuttonpanel.setPreferredSize(d);
        updatebuttonpanel.setMinimumSize(d);
        updatebuttonpanel.setMaximumSize(d);

        rightmainpanel.add(plotbuttonpanel);
        rightmainpanel.add(updatebuttonpanel);

    }

    private void init() {

        log.info("SensorAggregationParser initialized");
    }

    //    //    @Override
//    public ChartPanel getPlot() {
//        return getPlot();
//    }

    private ChartPanel getPlot(String sensor) {

        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries[] clustersSeries = getSeries(sensor);


        for (XYSeries clustersSery : clustersSeries) {
            dataset.addSeries(clustersSery);
        }


        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "time in seconds",
                "sensor value",
                dataset, PlotOrientation.VERTICAL, true, true, false);


        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        final int sensorCount = sensors.size();
        final int sensorClusterReadingsCount = sensorClusters.size();
        for (int i = sensorCount; i < sensorClusterReadingsCount; i++) {
            renderer.setSeriesLinesVisible(i, false);
        }
        chart.getXYPlot().setRenderer(renderer);


        JFreeChart chartTransformed = ChartFormater.transformChart(chart);
        return new ChartPanel(chartTransformed);

    }


    XYSeries[] getSeries(String sensor) {

        XYSeriesCollection seriesCollection = new XYSeriesCollection();

        for (final String cursensor : sensors.keySet()) {
            if (!cursensor.equals(sensor)) continue;
            XYSeries newseries = new XYSeries("Sensor " + sensor + " Value");
            HashMap<String, Double> SensorReadingMap = new HashMap<String, Double>();

            double avgReading = 0;
            for (final SensorReading sensorReading : sensorReadings) {
                if (sensorReading.getSensorName().equals(sensor)) {
                    log.debug("avgReading:" + avgReading + ", sensorReading.value:" + sensorReading.getSensorValue() + "@" + sensorReading.getUrn());
                    SensorReadingMap.put(sensorReading.getUrn(), sensorReading.getSensorValue());
                    avgReading = 0;
                    for (String key : SensorReadingMap.keySet()) {
                        avgReading += SensorReadingMap.get(key);

                    }
                    avgReading /= SensorReadingMap.keySet().size();

//                    avgReading = (int) ((((avgReading * readingsCount) + sensorReading.getSensorValue())) / (readingsCount + 1));
//                    avgReading = (int) ((((avgReading * (file.nodesize() - 1)) + sensorReading.getSensorValue())) / file.nodesize());
//                    avgReading = (int) ((avgReading + sensorReading.getSensorValue()) / 2);

                    log.debug("avg: " + avgReading);

                    newseries.add((int) (sensorReading.getTime() - file.starttime())/1000, avgReading);
                }
            }
            newseries.add((int) (file.getEnd_time() - file.starttime())/1000, avgReading);
            seriesCollection.addSeries(newseries);
        }


        Collections.sort(aggregatedReadings);
        for (String sensorCluster : sensorClusters.keySet()) {
            if (!sensorCluster.contains(sensor)) continue;
            XYSeries newseries = new XYSeries("Aggregated " + sensorCluster + " Value");

            final String sensorSeCluster = sensorCluster.substring(0, sensorCluster.indexOf("-"));
            final String sensorName = sensorCluster.substring(sensorCluster.indexOf("-"));
            log.debug(sensorName);
            log.debug(sensorSeCluster);
            for (final AggregatedSensorReading aggregatedSensorReading : aggregatedReadings) {
                if ((aggregatedSensorReading.getSeClusterID().equals(sensorSeCluster))
                        && (aggregatedSensorReading.getSensorName().equals(sensorName))) {
                    newseries.add((int) (aggregatedSensorReading.getTime() - file.starttime())/1000, aggregatedSensorReading.getSensorValue());
                }
            }

            seriesCollection.addSeries(newseries);
        }

        XYSeries[] series = new XYSeries[seriesCollection.getSeriesCount()];
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            series[i] = seriesCollection.getSeries(i);
        }

        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }


    //    @Override
    public void setTraceFile
    (TraceFile
             file) {
        this.file = file;

        DefaultListModel listModel = new DefaultListModel();

        for (String node : file.getNode_names()) {
            listModel.addElement(node);

        }
        JList nodeslist = new JList(listModel);
        rightmainpanel.add(new JLabel("Hidden Nodes:"));
        rightmainpanel.add(nodeslist);
        reset();
    }

//    void setTemplate(String template) {
//        parts = template.split(delimiter);
//        prefix = template.substring(0, template.indexOf(delimiter));
//        this.template = template;
//    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (m.time() < file.starttime() + startuptime * 1000) return;
        if (m.text().contains(sensorPrefix)) {
            final String text = m.text();
//            log.info("Sensor@" + m.time() + ":" + m.urn() + "\"" + text + "\"");
            final String[] parts = text.split(delimiter);

            final String sensorName = parts[1];
            final String sensorValue = parts[2];
            sensorReadings.add(new SensorReading(m.time(), sensorName, Double.parseDouble(sensorValue), m.urn()));
            sensors.put(sensorName, 1);
        } else if (m.text().contains(aggregatedPrefix)) {
            final String text = m.text();

            final String[] parts = text.split(delimiter);

            final String cluster = parts[1].substring(0, parts[1].indexOf("-"));
            final String sensorName = parts[1].substring(parts[1].indexOf("-"));
            final String sensorValue = parts[2];
//            log.info("AggregatedSensor@" + m.time() + ":" + cluster + "\"" + sensorName + "\"");
            aggregatedReadings.add(new AggregatedSensorReading(m.time(), sensorName, cluster, Double.parseDouble(sensorValue)));
            sensorClusters.put(cluster + sensorName, 1);

        }
    }

    void parse() {
        TraceReader a = new TraceReader(file);
        a.addObserver(this);
        a.run();
    }

    private void plot() {

        for (String sensor : sensors.keySet()) {
            JFrame jnew = new JFrame();
            jnew.add(getPlot(sensor));
            jnew.pack();
            jnew.setVisible(true);
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotbutton)) {
            reset();

            log.info("|=== parsing tracefile: " + file.filename() + "...");
            parse();
            log.info("|--- done parsing!");
            log.info("|=== generating plot...");
            plot();
            log.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(updatebutton)) {
            setDelimiter(delimitertextfield.getText());
        }
    }

    private void setDelimiter(String text) {
        delimiter = text;
    }

    private void reset() {
        sensorReadings = new ArrayList<SensorReading>();
        aggregatedReadings = new ArrayList<AggregatedSensorReading>();
        sensors = new Hashtable<String, Integer>();
        sensorClusters = new Hashtable<String, Integer>();
        init();
    }

    private class SensorReading implements Comparable {
        private final long time;
        private final String SensorName;
        private final double SensorValue;
        private final String urn;

        public String getUrn() {
            return urn;
        }

        private SensorReading(long time, String sensorName, double sensorValue, String urn) {
            this.time = time;
            SensorName = sensorName;
            SensorValue = sensorValue;
            this.urn = urn;
        }

        public long getTime() {
            return time;
        }

        public String getSensorName() {
            return SensorName;
        }

        public double getSensorValue() {
            return SensorValue;
        }

        public int compareTo(Object o) {
            final SensorReading other = (SensorReading) o;
            return (int) (getTime() - other.getTime());
        }
    }

    private class AggregatedSensorReading implements Comparable {
        private final long time;
        private final String SensorName;
        private final String SeClusterID;

        public String getSeClusterID() {
            return SeClusterID;
        }

        private final double SensorValue;

        private AggregatedSensorReading(long time, String sensorName, String seClusterID, double sensorValue) {
            this.time = time;
            SensorName = sensorName;
            SensorValue = sensorValue;
            SeClusterID = seClusterID;
        }

        public long getTime() {
            return time;
        }

        public String getSensorName() {
            return SensorName;
        }

        public double getSensorValue() {
            return SensorValue;
        }

        public int compareTo(Object o) {
            final AggregatedSensorReading other = (AggregatedSensorReading) o;
            return (int) (getTime() - other.getTime());
        }
    }
}
