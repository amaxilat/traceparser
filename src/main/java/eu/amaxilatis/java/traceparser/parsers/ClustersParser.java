package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.ChartFormater;
import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceReader;
import eu.amaxilatis.java.traceparser.panels.NodeSelectorPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ClustersParser extends GenericParser implements Observer, ActionListener {

    public static final String NAME = "Clusters Parser";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClustersParser.class);
    private static final String PLOT_TITLE = "Cluster Statistics";
    private static final String X_LABEL = "time in sec";
    private static final String Y_LABEL = "# of Clusters";

    private transient String template;
    private transient String prefix;
    private transient String delimiter = ";";
    private transient String[] parts;
    private final transient JButton plot;
    private final transient JButton remove;
    private final transient JTextField delimiterTf;
    private final transient JTextField templateTf;
    private final transient JTabbedPane tabbedPane;
    private final transient TextField plotTitleTf;
    private final transient TextField xLabelTf;
    private final transient TextField yLabelTf;

    private transient long duration;
    private transient Map<String, String>[] clusters;
    private transient int pcluster;
    private transient int pid;


    public ClustersParser(final JTabbedPane jTabbedPane1) {
        super(NAME);
        this.tabbedPane = jTabbedPane1;
        init();

        this.setLayout(new BorderLayout());

        delimiterTf = new JTextField(delimiter);
        templateTf = new JTextField(template);

        addLeft(new JLabel("delimiter"), delimiterTf);
        addLeft(new JLabel("Template"), templateTf);


        plot = new JButton(super.PLOT);
        plot.addActionListener(this);
        remove = new JButton(super.REMOVE);
        remove.addActionListener(this);
        addRight(plot, remove);


        plotTitleTf = new TextField(PLOT_TITLE);
        addRight(new JLabel("Plot title:"), plotTitleTf);
        xLabelTf = new TextField(X_LABEL);
        addRight(new JLabel("X axis Label:"), xLabelTf);
        yLabelTf = new TextField(Y_LABEL);
        addRight(new JLabel("Y axis Label:"), yLabelTf);

    }

    private void init() {

        setTemplate("CLP;ID;TYPE;CLUSTER");

        pcluster = 0;
        pid = 0;


        if (parts.length < 4) {
            LOGGER.error("invalid argument!!!");
        } else {
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("ID")) {
                    pid = i;
                } else if (parts[i].equals("CLUSTER")) {
                    pcluster = i;
                }
//                else if (parts[i].equals("TYPE")) {
//
//                }
            }
        }


        LOGGER.info("ClustersParser initialized");
    }

    public ChartPanel getPlot() {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] clustersSeries;
        clustersSeries = getSeries();

        for (XYSeries clustersSery : clustersSeries) {
            dataset.addSeries(clustersSery);
        }


        final JFreeChart chart = ChartFactory.createXYLineChart(
                plotTitleTf.getText(),
                xLabelTf.getText(),
                yLabelTf.getText(),
                dataset, PlotOrientation.VERTICAL, true, true, false);


        JFreeChart chartTransformed = ChartFormater.transformChart(chart);
        return new ChartPanel(chartTransformed);
    }

    public XYSeries[] getSeries() {

        XYSeries[] series = new XYSeries[2];
        series[0] = new XYSeries("Clusters");
        series[1] = new XYSeries("Avg. Size of Cluster");

        LOGGER.debug("%d",duration);
        for (int i = 0; i < duration; i++) {
            int clusterCount = 0;
            int simple_count = 0;
            LOGGER.debug("%d",clusters[i].keySet().size());
            for (String key : clusters[i].keySet()) {

                if (clusters[i].get(key).equals(key)) {
                    LOGGER.debug(key + " - " + clusters[i].get(key) + " N");
                    clusterCount++;
                } else {
                    LOGGER.debug(key + " - " + clusters[i].get(key));
                    simple_count++;
                }
            }


            series[0].add(i, clusterCount);
            if (clusterCount > 0) {
                //LOGGER.info("Clusters : " + clusterCount + " clSize : " + (simple_count + clusterCount) / clusterCount);
                series[1].add(i, (simple_count + clusterCount) / clusterCount);
            } else {
                //LOGGER.info("Clusters : " + clusterCount + " clSize : 0");
                series[1].add(i, 0);
            }
            LOGGER.debug("setting " + i);
        }

        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }

    void setTemplate(final String template) {
        parts = template.split(delimiter);
        prefix = template.substring(0, template.indexOf(delimiter));
        this.template = template;
    }

    public void update(final Observable observable, final Object obj) {
        final TraceMessage message = (TraceMessage) obj;
        if (!NodeSelectorPanel.isSelected(message.getUrn())) {
            return;
        }

        LOGGER.debug(message.getText());
        if (message.getText().startsWith(prefix)) {
            LOGGER.debug("Cluster@" + message.getTime() + ":" + message.getUrn());
            final String[] mess = message.getText().split(delimiter);
            setCluster(mess[pid], mess[pcluster], ((int) ((message.getTime() - TraceFile.getInstance().getStartTime()) / 1000)));
        }
    }

    private void setCluster(final String node, final String cluster, final int time) {
        LOGGER.debug(node + "-" + cluster + "@" + time);
        for (int i = time; i < duration - 1; i++) {
            clusters[i].put(node, cluster);
        }
    }

    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plot)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + TraceFile.getInstance().getFilename() + "...");
            TraceReader reader = new TraceReader();
            reader.addObserver(this);
            reader.run();
            LOGGER.info("|--- done parsing!");
            LOGGER.info("|=== generating plot...");
            JFrame frame = new JFrame();
            frame.add(getPlot());
            frame.pack();
            frame.setVisible(true);
            LOGGER.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(remove)) {
            tabbedPane.remove(this);
        }
    }

    private void reset() {
        delimiter = delimiterTf.getText();
        template = templateTf.getText();

        duration = (int) (TraceFile.getInstance().getDuration() / 1000 + 1);
        clusters = new HashMap[(int) duration];

        for (int i = 0; i < duration; i++) {
            clusters[i] = new HashMap<String, String>();
            clusters[i].clear();
        }

        init();
    }
}
