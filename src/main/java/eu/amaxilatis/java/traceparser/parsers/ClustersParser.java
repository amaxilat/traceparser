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

    public static final String NAME = "Clusters Parser";
    private static final Logger LOGGER = Logger.getLogger(ClustersParser.class);
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
    private transient TraceFile file;


    public ClustersParser(final JTabbedPane jTabbedPane1) {
        this.tabbedPane = jTabbedPane1;
        init();

        this.setLayout(new BorderLayout());

        final JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        final JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        final JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel(NAME), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);


        delimiterTf = new JTextField(delimiter);
        templateTf = new JTextField(template);

        leftmainpanel.add(new CouplePanel(new JLabel("delimiter"), delimiterTf));
        leftmainpanel.add(new CouplePanel(new JLabel("Template"), templateTf));


        plot = new JButton(super.PLOT);
        plot.addActionListener(this);
        remove = new JButton(super.REMOVE);
        remove.addActionListener(this);
        rightmainpanel.add(new CouplePanel(plot, remove));


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

        LOGGER.debug(duration);
        for (int i = 0; i < duration; i++) {
            int clusterCount = 0;
            int simple_count = 0;
            LOGGER.debug(clusters[i].keySet().size());
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

    //    @Override
    public void setTraceFile(final TraceFile file) {
        this.file = file;

        reset();
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
            setCluster(mess[pid], mess[pcluster], ((int) ((message.getTime() - file.getStartTime()) / 1000)));
        }
    }

    private void setCluster(final String node,final String cluster,final int time) {
        LOGGER.debug(node + "-" + cluster + "@" + time);
        for (int i = time; i < duration - 1; i++) {
            clusters[i].put(node, cluster);
        }
    }

    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plot)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + file.getFilename() + "...");
            TraceReader reader = new TraceReader(file);
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

        duration = (int) (file.getDuration() / 1000 + 1);
        clusters = new HashMap[(int) duration];

        for (int i = 0; i < duration; i++) {
            clusters[i] = new HashMap<String, String>();
            clusters[i].clear();
        }

        init();
    }
}
