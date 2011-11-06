package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.ChartFormater;
import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceReader;
import eu.amaxilatis.java.traceparser.panels.NodeSelectorPanel;
import eu.amaxilatis.java.traceparser.panels.CouplePanel;
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

    private static final Logger LOGGER = Logger.getLogger(SensorAggregationParser.class);
    public static final String NAME = "SensorAggregation Parser";
    private final static String DELIMITER = ":";
    private static final String SENS_PREFIX = "Sread";
    private static final String AG_PREFIX = "Sval";

    private transient final JButton plotButton;
    private transient final JButton removeButton;
    private transient final JTabbedPane tabbedPane;
    private transient final JTextField delimiterTf;
    private transient final JTextField plotTitleTf;
    private transient final JTextField xLabelTf;
    private transient final JTextField yLabelTf;
    private transient final JTextField meanTf;
    private transient final JTextField agTf;
    private transient final JTextField startTf;

    private TraceFile file;

    private java.util.List<SensorReading> senReadings = new ArrayList<SensorReading>();
    private java.util.List<AggregatedSensorReading> agReadings = new ArrayList<AggregatedSensorReading>();
    private Hashtable<String, Integer> sensors = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> senClusters = new Hashtable<String, Integer>();
    private final int startTime = 100;
    private Map<String, String> semantics = new HashMap<String, String>();


    public SensorAggregationParser(final JTabbedPane jTabbedPane1) {
        tabbedPane = jTabbedPane1;
        init();
        this.setLayout(new BorderLayout());

        final JPanel mainPanel = new JPanel(new GridLayout(0, 2, 30, 30));
        final JPanel leftMainPanel = new JPanel(new GridLayout(0, 1));
        final JPanel rightMainPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.add(leftMainPanel);
        mainPanel.add(rightMainPanel);

        this.add(new JLabel(NAME), BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);


        plotButton = new JButton(super.PLOT);
        plotButton.addActionListener(this);
        removeButton = new JButton(super.REMOVE);
        removeButton.addActionListener(this);
        rightMainPanel.add(new CouplePanel(plotButton, removeButton));

        delimiterTf = new JTextField(DELIMITER);
        meanTf = new JTextField(SENS_PREFIX);
        agTf = new JTextField(AG_PREFIX);
        startTf = new JTextField("" + startTime);

        leftMainPanel.add(new CouplePanel(new JLabel("delimiter"), delimiterTf));
        leftMainPanel.add(new CouplePanel(new JLabel("Mean Value"), meanTf));
        leftMainPanel.add(new CouplePanel(new JLabel("Aggregated Value"), agTf));
        leftMainPanel.add(new CouplePanel(new JLabel("Startup Time"), startTf));


        plotTitleTf = new JTextField("Semantics Statistics");
        rightMainPanel.add(new CouplePanel(new JLabel("Plot title:"), plotTitleTf));
        xLabelTf = new JTextField("getTime in sec");
        rightMainPanel.add(new CouplePanel(new JLabel("X axis Label:"), xLabelTf));
        yLabelTf = new JTextField("Sensor Value");
        rightMainPanel.add(new CouplePanel(new JLabel("Y axis Label:"), yLabelTf));


    }

    private void init() {
        semantics.put("212", "PIR");
        semantics.put("211", "TEMP");
        semantics.put("210", "LIGHT");
        semantics.put("10", "SCREEN");
        semantics.put("5", "SIDE");
        semantics.put("4", "INROOM");
        semantics.put("3", "SECTOR");
        semantics.put("2", "FLOOR");
        semantics.put("1", "BUILDING");


        LOGGER.info("SensorAggregationParser initialized");
    }

    private ChartPanel getPlot(final String sensor) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        final XYSeries[] clustersSeries = getSeries(sensor);

        for (XYSeries clustersSery : clustersSeries) {
            dataset.addSeries(clustersSery);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                plotTitleTf.getText(),
                xLabelTf.getText(),
                yLabelTf.getText(),
                dataset, PlotOrientation.VERTICAL, true, true, false);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        final int sensorCount = sensors.size();
        final int sensorClusterReadingsCount = senClusters.size();
        for (int i = sensorCount; i < sensorClusterReadingsCount; i++) {
            renderer.setSeriesLinesVisible(i, false);
        }
        chart.getXYPlot().setRenderer(renderer);


        JFreeChart chartTransformed = ChartFormater.transformChart(chart);
        return new ChartPanel(chartTransformed);

    }


    XYSeries[] getSeries(final String sensor) {

        XYSeriesCollection seriesCollection = new XYSeriesCollection();

        for (final String cursensor : sensors.keySet()) {
            if (cursensor.equals(sensor)) {
                String s2 = "Sensor  " + sensor + "  Value";
                for (String key : semantics.keySet()) {
                    s2 = s2.replaceFirst(" " + key + " ", semantics.get(key));
                }
                XYSeries newseries = new XYSeries(s2);
                HashMap<String, Double> SensorReadingMap = new HashMap<String, Double>();

                double avgReading = 0;
                for (final SensorReading sensorReading : senReadings) {
                    if (sensorReading.getSensorName().equals(sensor)) {
                        LOGGER.debug("avgReading:" + avgReading + ", sensorReading.value:" + sensorReading.getSensorValue() + "@" + sensorReading.getUrn());
                        SensorReadingMap.put(sensorReading.getUrn(), sensorReading.getSensorValue());
                        avgReading = 0;
                        for (String key : SensorReadingMap.keySet()) {
                            avgReading += SensorReadingMap.get(key);

                        }
                        avgReading /= SensorReadingMap.keySet().size();

//                    avgReading = (int) ((((avgReading * readingsCount) + sensorReading.getSensorValue())) / (readingsCount + 1));
//                    avgReading = (int) ((((avgReading * (file.getNodeSize() - 1)) + sensorReading.getSensorValue())) / file.getNodeSize());
//                    avgReading = (int) ((avgReading + sensorReading.getSensorValue()) / 2);

                        LOGGER.debug("avg: " + avgReading);

                        newseries.add((int) (sensorReading.getTime() - file.getStartTime()) / 1000, avgReading);
                    }
                }
                newseries.add((int) (file.getEndTime() - file.getStartTime()) / 1000, avgReading);
                seriesCollection.addSeries(newseries);
            }
        }


        Collections.sort(agReadings);
        for (String sensorCluster : senClusters.keySet()) {
            if (sensorCluster.contains(sensor)) {


                String s2 = "Aggregated " + sensorCluster + " Value";
                for (String key : semantics.keySet()) {
                    s2 = s2.replaceFirst("-" + key + " ", "-" + semantics.get(key) + " ");
                }

                final XYSeries newseries = new XYSeries(s2);

                final String sensorSeCluster = sensorCluster.substring(0, sensorCluster.indexOf("-"));
                final String sensorName = sensorCluster.substring(sensorCluster.indexOf("-"));
                LOGGER.debug(sensorName);
                LOGGER.debug(sensorSeCluster);
                for (final AggregatedSensorReading aggregatedSensorReading : agReadings) {
                    if ((aggregatedSensorReading.getSeClusterID().equals(sensorSeCluster))
                            && (aggregatedSensorReading.getSensorName().equals(sensorName))) {
                        newseries.add((int) (aggregatedSensorReading.getTime() - file.getStartTime()) / 1000, aggregatedSensorReading.getSensorValue());
                    }
                }

                seriesCollection.addSeries(newseries);
            }
        }

        XYSeries[] series = new XYSeries[seriesCollection.getSeriesCount()];
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            series[i] = seriesCollection.getSeries(i);
        }

        return series;
    }


    //    @Override
    public void setTraceFile
    (TraceFile
             file) {
        this.file = file;

        DefaultListModel listModel = new DefaultListModel();

        for (String node : file.getNodeNames()) {
            listModel.addElement(node);

        }


        reset();
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (!NodeSelectorPanel.isSelected(m.getUrn())) return;
        if (m.getTime() < file.getStartTime() + startTime * 1000) return;
        if (m.getText().contains(meanTf.getText())) {
            final String text = m.getText();
//            LOGGER.info("Sensor@" + m.getTime() + ":" + m.getUrn() + "\"" + text + "\"");
            final String[] parts = text.split(delimiterTf.getText());

            final String sensorName = parts[1];
            final String sensorValue = parts[2];
            senReadings.add(new SensorReading(m.getTime(), sensorName, Double.parseDouble(sensorValue), m.getUrn()));
            sensors.put(sensorName, 1);
        } else if (m.getText().contains(agTf.getText())) {
            final String text = m.getText();

            final String[] parts = text.split(delimiterTf.getText());

            final String cluster = parts[1].substring(0, parts[1].indexOf("-"));
            final String sensorName = parts[1].substring(parts[1].indexOf("-"));
            final String sensorValue = parts[2];
//            LOGGER.info("AggregatedSensor@" + m.getTime() + ":" + cluster + "\"" + sensorName + "\"");
            agReadings.add(new AggregatedSensorReading(m.getTime(), sensorName, cluster, Double.parseDouble(sensorValue)));
            senClusters.put(cluster + sensorName, 1);

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

    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotButton)) {
            reset();

            LOGGER.info("|=== parsing tracefile: " + file.getFilename() + "...");
            parse();
            LOGGER.info("|--- done parsing!");
            LOGGER.info("|=== generating plot...");
            plot();
            LOGGER.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(removeButton)) {
            tabbedPane.remove(this);
        }
    }

    private void reset() {
        senReadings = new ArrayList<SensorReading>();
        agReadings = new ArrayList<AggregatedSensorReading>();
        sensors = new Hashtable<String, Integer>();
        senClusters = new Hashtable<String, Integer>();
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

        private SensorReading(final long time, final String sensorName, final double sensorValue, final String urn) {
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

        public int compareTo(final Object object) {
            final SensorReading other = (SensorReading) object;
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

        private AggregatedSensorReading(final long time, final String sensorName, final String seClusterID, final double sensorValue) {
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

        public int compareTo(final Object object) {
            final AggregatedSensorReading other = (AggregatedSensorReading) object;
            return (int) (getTime() - other.getTime());
        }
    }
}
