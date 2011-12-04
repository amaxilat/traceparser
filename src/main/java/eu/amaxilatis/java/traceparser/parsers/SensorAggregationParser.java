package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.traces.AbstractTraceMessage;
import eu.amaxilatis.java.traceparser.ChartFormatter;
import eu.amaxilatis.java.traceparser.traces.TraceFile;
import eu.amaxilatis.java.traceparser.traces.TraceReader;
import eu.amaxilatis.java.traceparser.panels.NodeSelectorPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 *
 */
public class SensorAggregationParser extends GenericParser implements Observer, ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorAggregationParser.class);
    public static final String NAME = "SensorAggregation Parser";
    private static final String DELIMITER = ":";
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

    private java.util.List<SensorReading> senReadings = new ArrayList<SensorReading>();
    private java.util.List<AggregatedSensorReading> agReadings = new ArrayList<AggregatedSensorReading>();
    private Hashtable<String, Integer> sensors = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> senClusters = new Hashtable<String, Integer>();
    private final int startTime = 100;
    private Map<String, String> semantics = new HashMap<String, String>();

    /**
     * @param jTabbedPane1
     */
    public SensorAggregationParser(final JTabbedPane jTabbedPane1) {
        super(NAME);
        tabbedPane = jTabbedPane1;
        init();

        plotButton = new JButton(super.PLOT);
        plotButton.addActionListener(this);
        removeButton = new JButton(super.REMOVE);
        removeButton.addActionListener(this);
        addRight(plotButton, removeButton);

        delimiterTf = new JTextField(DELIMITER);
        meanTf = new JTextField(SENS_PREFIX);
        agTf = new JTextField(AG_PREFIX);
        startTf = new JTextField("" + startTime);

        addLeft(new JLabel("delimiter"), delimiterTf);
        addLeft(new JLabel("Mean Value"), meanTf);
        addLeft(new JLabel("Aggregated Value"), agTf);
        addLeft(new JLabel("Startup Time"), startTf);


        plotTitleTf = new JTextField("Semantics Statistics");
        addRight(new JLabel("Plot title:"), plotTitleTf);
        xLabelTf = new JTextField("getTime in sec");
        addRight(new JLabel("X axis Label:"), xLabelTf);
        yLabelTf = new JTextField("Sensor Value");
        addRight(new JLabel("Y axis Label:"), yLabelTf);
    }

    /**
     *
     */
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

    /**
     * @param sensor
     * @return
     */
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


        JFreeChart chartTransformed = ChartFormatter.transformChart(chart);
        return new ChartPanel(chartTransformed);

    }

    /**
     * @param sensor
     * @return
     */
    XYSeries[] getSeries(final String sensor) {

        final XYSeriesCollection seriesCollection = new XYSeriesCollection();


        String sensTitle = "Sensor  " + sensor + "  Value";
        for (String key : semantics.keySet()) {
            sensTitle = sensTitle.replaceFirst(" " + key + " ", semantics.get(key));
        }
        XYSeries newseries = new XYSeries(sensTitle);
        final HashMap<String, Double> SensorReadingMap = new HashMap<String, Double>();

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

                newseries.add((int) (sensorReading.getTime() - TraceFile.getInstance().getStartTime()) / 1000, avgReading);
            }
        }
        newseries.add((int) (TraceFile.getInstance().getEndTime() - TraceFile.getInstance().getStartTime()) / 1000, avgReading);
        seriesCollection.addSeries(newseries);

        Collections.sort(agReadings);
        for (String sensorCluster : senClusters.keySet()) {
            if (sensorCluster.contains(sensor)) {


                sensTitle = "Aggregated " + sensorCluster + " Value";
                for (String key : semantics.keySet()) {
                    sensTitle = sensTitle.replaceFirst("-" + key + " ", "-" + semantics.get(key) + " ");
                }

                newseries = new XYSeries(sensTitle);

                final String sensorSeCluster = sensorCluster.substring(0, sensorCluster.indexOf("-"));
                final String sensorName = sensorCluster.substring(sensorCluster.indexOf("-"));
                LOGGER.debug(sensorName);
                LOGGER.debug(sensorSeCluster);
                for (final AggregatedSensorReading aggregatedSensorReading : agReadings) {
                    if ((aggregatedSensorReading.getSeClusterID().equals(sensorSeCluster))
                            && (aggregatedSensorReading.getSensorName().equals(sensorName))) {
                        newseries.add((int) (aggregatedSensorReading.getTime() - TraceFile.getInstance().getStartTime()) / 1000, aggregatedSensorReading.getSensorValue());
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

    /**
     * @param observable
     * @param object
     */
    public void update(final Observable observable, final Object object) {
        final AbstractTraceMessage message = (AbstractTraceMessage) object;
        if (NodeSelectorPanel.isSelected(message.getUrn())) {

            if (message.getTime() < TraceFile.getInstance().getStartTime() + startTime * 1000) return;
            if (message.getText().contains(meanTf.getText())) {
                final String text = message.getText();
//            LOGGER.info("Sensor@" + message.getTime() + ":" + message.getUrn() + "\"" + text + "\"");
                final String[] parts = text.split(delimiterTf.getText());

                final String sensorName = parts[1];
                final String sensorValue = parts[2];
                senReadings.add(new SensorReading(message.getTime(), sensorName, Double.parseDouble(sensorValue), message.getUrn()));
                sensors.put(sensorName, 1);
            } else if (message.getText().contains(agTf.getText())) {
                final String text = message.getText();

                final String[] parts = text.split(delimiterTf.getText());

                final String cluster = parts[1].substring(0, parts[1].indexOf('-'));
                final String sensorName = parts[1].substring(parts[1].indexOf('-'));
                final String sensorValue = parts[2];
//            LOGGER.info("AggregatedSensor@" + message.getTime() + ":" + cluster + "\"" + sensorName + "\"");
                agReadings.add(new AggregatedSensorReading(message.getTime(), sensorName, cluster, Double.parseDouble(sensorValue)));
                senClusters.put(cluster + sensorName, 1);

            }
        }
    }

    /**
     *
     */
    void parse() {
        final TraceReader reader = new TraceReader();
        reader.addObserver(this);
        reader.run();
    }

    /**
     *
     */
    private void plot() {
        for (String sensor : sensors.keySet()) {
            final JFrame frame = new JFrame();
            frame.add(getPlot(sensor));
            frame.pack();
            frame.setVisible(true);
        }
    }

    /**
     * @param actionEvent
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotButton)) {
            reset();

            LOGGER.info("|=== parsing tracefile: " + TraceFile.getInstance().getFilename() + "...");
            parse();
            LOGGER.info("|--- done parsing!");
            LOGGER.info("|=== generating plot...");
            plot();
            LOGGER.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(removeButton)) {
            tabbedPane.remove(this);
        }
    }

    /**
     *
     */
    private void reset() {
        senReadings = new ArrayList<SensorReading>();
        agReadings = new ArrayList<AggregatedSensorReading>();
        sensors = new Hashtable<String, Integer>();
        senClusters = new Hashtable<String, Integer>();
        init();
    }

    /**
     *
     */
    private class SensorReading implements Comparable {
        private final transient long time;
        private final transient String SensorName;
        private final transient double SensorValue;
        private final transient String urn;

        /**
         * @return
         */
        public String getUrn() {
            return urn;
        }

        /**
         * @param time
         * @param sensorName
         * @param sensorValue
         * @param urn
         */
        public SensorReading(final long time, final String sensorName, final double sensorValue, final String urn) {
            this.time = time;
            SensorName = sensorName;
            SensorValue = sensorValue;
            this.urn = urn;
        }

        /**
         * @return
         */
        public long getTime() {
            return time;
        }

        /**
         * @return
         */
        public String getSensorName() {
            return SensorName;
        }

        /**
         * @return
         */
        public double getSensorValue() {
            return SensorValue;
        }

        /**
         * @param object
         * @return
         */
        public int compareTo(final Object object) {
            final SensorReading other = (SensorReading) object;
            return (int) (getTime() - other.getTime());
        }
    }

    /**
     *
     */
    private class AggregatedSensorReading implements Comparable {
        private final transient long time;
        private final transient String SensorName;
        private final transient String SeClusterID;

        /**
         * @return
         */
        public String getSeClusterID() {
            return SeClusterID;
        }

        /**
         *
         */
        private final double SensorValue;

        /**
         * @param time
         * @param sensorName
         * @param seClusterID
         * @param sensorValue
         */
        public AggregatedSensorReading(final long time, final String sensorName, final String seClusterID, final double sensorValue) {
            this.time = time;
            SensorName = sensorName;
            SensorValue = sensorValue;
            SeClusterID = seClusterID;
        }

        /**
         * @return
         */
        public long getTime() {
            return time;
        }

        /**
         * @return
         */
        public String getSensorName() {
            return SensorName;
        }

        /**
         * @return
         */
        public double getSensorValue() {
            return SensorValue;
        }

        /**
         * @param object
         * @return
         */
        public int compareTo(final Object object) {
            final AggregatedSensorReading other = (AggregatedSensorReading) object;
            return (int) (getTime() - other.getTime());
        }
    }
}
