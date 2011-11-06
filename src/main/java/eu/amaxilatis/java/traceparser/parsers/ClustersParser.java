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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ClustersParser extends AbstractParser implements Observer, ActionListener {

    private static final Logger LOGGER = Logger.getLogger(ClustersParser.class);
    public static final String NAME = "Clusters Parser";
    private static final String PLOT_TITLE = "Cluster Statistics";
    private static final String X_LABEL = "time in sec";
    private static final String Y_LABEL = "# of Clusters";

    private String template;
    private String prefix;
    private String delimiter = ";";
    private String[] parts;
    private final JButton plotbutton;
    private final JButton removeButton;
    private final JTextField delimiterTf;
    private final JTextField templateTf;
    private final JTabbedPane tabbedPane;
    private final TextField plotTitleTf;
    private final TextField xLabelTf;
    private final TextField yLabelTf;

    private long duration;
    private Map<String, String>[] clusters;
    private int pcluster;
    private int pid;
    private TraceFile file;


    public ClustersParser(JTabbedPane jTabbedPane1) {
        this.tabbedPane = jTabbedPane1;
        init();

        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel(NAME), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);


        delimiterTf = new JTextField(delimiter);
        templateTf = new JTextField(template);

        leftmainpanel.add(new CouplePanel(new JLabel("delimiter"), delimiterTf));
        leftmainpanel.add(new CouplePanel(new JLabel("Template"), templateTf));


        plotbutton = new JButton(super.PLOT);
        plotbutton.addActionListener(this);
        removeButton = new JButton(super.REMOVE);
        removeButton.addActionListener(this);
        rightmainpanel.add(new CouplePanel(plotbutton, removeButton));


        plotTitleTf = new TextField(PLOT_TITLE);
        rightmainpanel.add(new CouplePanel(new JLabel("Plot title:"), plotTitleTf));
        xLabelTf = new TextField(X_LABEL);
        rightmainpanel.add(new CouplePanel(new JLabel("X axis Label:"), xLabelTf));
        yLabelTf = new TextField(Y_LABEL);
        rightmainpanel.add(new CouplePanel(new JLabel("Y axis Label:"), yLabelTf));

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
                } else if (parts[i].equals("TYPE")) {

                }
            }
        }


        LOGGER.info("ClustersParser initialized");
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


        JFreeChart chartTransformed = ChartFormater.transformChart(chart);
        return new ChartPanel(chartTransformed);
    }

    //    @Override
    public ChartPanel getPlot() {
        return getPlot(false, false, "", "", "");
    }

    public XYSeries[] getSeries() {

        XYSeries[] series = new XYSeries[2];
        series[0] = new XYSeries("Clusters");
        series[1] = new XYSeries("Avg. Size of Cluster");

        LOGGER.debug(duration);
        for (int i = 0; i < duration; i++) {
            int cluster_count = 0;
            int simple_count = 0;
            LOGGER.info(clusters[i].keySet().size());
            for (String key : clusters[i].keySet()) {

                if (clusters[i].get(key).equals(key)) {
                    LOGGER.info(key + " - " + clusters[i].get(key) + " N");
                    cluster_count++;
                } else {
                    LOGGER.info(key + " - " + clusters[i].get(key));
                    simple_count++;
                }
            }


            series[0].add(i, cluster_count);
            if (cluster_count > 0) {
                //LOGGER.info("Clusters : " + cluster_count + " clSize : " + (simple_count + cluster_count) / cluster_count);
                series[1].add(i, (simple_count + cluster_count) / cluster_count);
            } else {
                //LOGGER.info("Clusters : " + cluster_count + " clSize : 0");
                series[1].add(i, 0);
            }
            LOGGER.debug("setting " + i);
        }

        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public XYSeries[] getSeries_aggregate() {
        return getSeries();
    }

    //    @Override
    public void setTraceFile(TraceFile file) {
        this.file = file;

        reset();
    }

    void setTemplate(String template) {
        parts = template.split(delimiter);
        prefix = template.substring(0, template.indexOf(delimiter));
        this.template = template;
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (!NodeSelectorPanel.isSelected(m.getUrn())) return;

        LOGGER.debug(m.getText());
        if (m.getText().startsWith(prefix)) {
            LOGGER.info("Cluster@" + m.getTime() + ":" + m.getUrn());
            final String[] mess = m.getText().split(delimiter);
            set_cluster(mess[pid], mess[pcluster], ((int) ((m.getTime() - file.getStartTime()) / 1000)));
        }
    }

    private void set_cluster(String node, String clust, int time) {
        LOGGER.debug(node + "-" + clust + "@" + time);
        for (int i = time; i < duration - 1; i++) {
            clusters[i].put(node, clust);
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotbutton)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + file.getFilename() + "...");
            TraceReader a = new TraceReader(file);
            a.addObserver(this);
            a.run();
            LOGGER.info("|--- done parsing!");
            LOGGER.info("|=== generating plot...");
            JFrame jnew = new JFrame();
            jnew.add(getPlot());
            jnew.pack();
            jnew.setVisible(true);
            LOGGER.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(removeButton)) {
            tabbedPane.remove(this);
        }
    }

    private void reset() {
        delimiter = delimiterTf.getText();
        template = templateTf.getText();

        duration = (int) (file.getDuration() / 1000 + 1);
        clusters = new HashMap[(int) duration];

        for (int i = 0; i < duration; i++) {
            clusters[i] = new HashMap<String, String>();
            clusters[i].clear();
        }

        init();
    }
}
