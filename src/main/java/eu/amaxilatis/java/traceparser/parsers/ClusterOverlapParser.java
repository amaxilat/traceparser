package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.ChartFormater;
import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceReader;
import eu.amaxilatis.java.traceparser.panels.CouplePanel;
import eu.amaxilatis.java.traceparser.panels.NodeSelectorPanel;
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

public class ClusterOverlapParser extends AbstractParser implements Observer, ActionListener {

    private static final Logger LOGGER = Logger.getLogger(ClustersParser.class);
    public static final String NAME = "ClusterOverlap Parser";
    private static final String PLOT_TITLE = "Cluster Overlap Statistics";
    private static final String X_LABEL = "time in sec";
    private static final String Y_LABEL = "# of Clusters";

    private final JButton plotButton;
    private final JButton removeButton;
    private final JTextField delimiterTf;
    private final JTextField templateTf;
    private final JTabbedPane tabbedPane;
    private final JTextField plotTitleTf;
    private final JTextField xLabelTf;
    private final JTextField yLabelTf;
    private final JTextField clTypeTf;

    private long duration;
    private Map<String, Node> nodes;
    private Map<String, SemanticEntity> semanticEntityMap;
    private int pid, pcluster, pparent, ptname, ptid;
    private String template;
    private String prefix;
    private String clType = "NAME-ID";
    private String delimiter = ";";
    private String[] parts;
    private String clTypeDelimiter = "-";
    int prev = 0;

    XYSeries avgClusters = new XYSeries("avg SE per node");


    public ClusterOverlapParser(JTabbedPane jTabbedPane1) {
        this.tabbedPane = jTabbedPane1;

        setTemplate("CLL;ID;CLUSTER;PARENT");

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
        clTypeTf = new JTextField(clType);

        leftmainpanel.add(new CouplePanel(new JLabel("delimiter"), delimiterTf));
        leftmainpanel.add(new CouplePanel(new JLabel("Template"), templateTf));
        leftmainpanel.add(new CouplePanel(new JLabel("Cluster Template"), clTypeTf));


        plotButton = new JButton(super.PLOT);
        plotButton.addActionListener(this);
        removeButton = new JButton(super.REMOVE);
        removeButton.addActionListener(this);
        rightmainpanel.add(new CouplePanel(plotButton, removeButton));


        plotTitleTf = new JTextField(PLOT_TITLE);
        rightmainpanel.add(new CouplePanel(new JLabel("Plot title:"), plotTitleTf));
        xLabelTf = new JTextField(X_LABEL);
        rightmainpanel.add(new CouplePanel(new JLabel("X axis Label:"), xLabelTf));
        yLabelTf = new JTextField(Y_LABEL);
        rightmainpanel.add(new CouplePanel(new JLabel("Y axis Label:"), yLabelTf));


        init();
    }

    private void init() {


        pid = 0;
        pcluster = 0;
        pparent = 0;

        if (parts.length < 4) {
            LOGGER.error("invalid argument!!!");
        } else {
            for (int i = 0; i < parts.length; i++) {
                if ("ID".equals(parts[i])) {
                    pid = i;
                } else if ("CLUSTER".equals(parts[i])) {
                    pcluster = i;
                } else if ("PARENT".equals(parts[i])) {
                    pparent = i;
                }
            }
        }

        LOGGER.info("pid:" + pid + ",pcluster:" + pcluster + "pparent:" + pparent);

        final String[] tparts = clTypeTf.getText().split(clTypeDelimiter);
        for (int i = 0; i < tparts.length; i++) {
            if ("NAME".equals(tparts[i])) {
                ptname = i;
            } else if ("ID".equals(tparts[i])) {
                ptid = i;
            }
        }

        LOGGER.info("ptname:" + ptname + ",ptid:" + ptid);

        LOGGER.info("ClustersParser initialized");
    }

    public ChartPanel getPlot(boolean aggregate, String title, String xlabel, String ylabel) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] clustersSeries;

        clustersSeries = getSeries();


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

    public ChartPanel getPlot() {

        for (final String entry : semanticEntityMap.keySet()) {
            LOGGER.info("SE : " + entry + "contains :" + semanticEntityMap.get(entry).countNodes());
        }

        return getPlot(false, "", "", "");
    }

    public XYSeries[] getSeries() {

//        XYSeries[] series = new XYSeries[2];
//        series[0] = new XYSeries("Clusters");
//        series[1] = new XYSeries("Avg. Size of Cluster");

//        LOGGER.debug(duration);
//        for (int i = 0; i < duration; i++) {
//            int cluster_count = 0;
//            int simple_count = 0;
////            LOGGER.debug(clusters[i].keySet().size());
////            for (String key : clusters[i].keySet()) {
////
////                if (clusters[i].get(key).equals(key)) {
////                    LOGGER.debug(key + " - " + clusters[i].get(key) + " N");
////                    cluster_count++;
////                } else {
////                    LOGGER.debug(key + " - " + clusters[i].get(key));
////                    simple_count++;
////                }
////            }
//
//
//            series[0].add(i, cluster_count);
//            if (cluster_count > 0) {
//                //LOGGER.info("Clusters : " + cluster_count + " clSize : " + (simple_count + cluster_count) / cluster_count);
//                series[1].add(i, (simple_count + cluster_count) / cluster_count);
//            } else {
//                //LOGGER.info("Clusters : " + cluster_count + " clSize : 0");
//                series[1].add(i, 0);
//            }
//            LOGGER.debug("setting " + i);
//        }
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(avgClusters);

        XYSeries[] series = new XYSeries[collection.getSeriesCount()];
        for (int i = 0; i < collection.getSeriesCount(); i++) {
            series[i] = collection.getSeries(i);
        }

        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public XYSeries[] getSeries_aggregate() {
        return getSeries();
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
//            LOGGER.debug("Cluster@" + m.getTime() + ":" + m.getUrn());
            final String[] mess = m.getText().split(delimiter);
            setCluster(mess[pid], mess[pcluster].split(clTypeDelimiter)[ptname], ((int) ((m.getTime() - TraceFile.getInstance().getStartTime()) / 1000)));
            setSemanticEntity(mess[pcluster].split(clTypeDelimiter)[ptname], mess[pid], ((int) ((m.getTime() - TraceFile.getInstance().getStartTime()) / 1000)));
        }
    }

    private void setSemanticEntity(String semanticEntity, String id, int time) {
        if (semanticEntityMap.containsKey(semanticEntity)) {
            semanticEntityMap.get(semanticEntity).addNode(id);
        } else {
            final SemanticEntity semanticEntityEntry = new SemanticEntity(semanticEntity);
            semanticEntityEntry.addNode(id);
            semanticEntityMap.put(semanticEntity, semanticEntityEntry);
        }
    }

    private void setCluster(String node, String clust, int time) {
        Node tempNode;
        if (nodes.containsKey(node)) {
            tempNode = nodes.get(node);
            tempNode.setSemantic(clust, "1");
        } else {
            tempNode = new Node(node);
            tempNode.setSemantic(clust, "1");
        }
        nodes.put(node, tempNode);

        set2series(time);
        LOGGER.debug(node + "-" + tempNode.countSemantics() + "@" + time);
    }

    private void set2series(long time) {
        double totalClusters = 0;
        final double totalNodes = nodes.size();

        for (final String nUrn : nodes.keySet()) {
            totalClusters += nodes.get(nUrn).countSemantics();
        }

        if (totalNodes > 0) {
            final double value = Math.ceil(totalClusters / totalNodes);
            avgClusters.addOrUpdate(time - 1, prev);

            prev = (int) value;
            avgClusters.addOrUpdate(time, value);
            LOGGER.debug(value + "@" + time);
        } else {

            avgClusters.addOrUpdate(time, prev);
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotButton)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + TraceFile.getInstance().getFilename() + "...");
            TraceReader a = new TraceReader();
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

        duration = (int) (TraceFile.getInstance().getDuration() / 1000 + 1);


        nodes = new HashMap<String, Node>();
        semanticEntityMap = new HashMap<String, SemanticEntity>();
        init();
    }

    private class Node {
        private final String id;
        private Map<String, String> semantics;

        private Node(String id) {
            this.id = id;
            semantics = new HashMap<String, String>();
        }

        public String getId() {
            return id;
        }

        public void setSemantic(String semantic, String semid) {
            if (semantics.containsKey(semantic)) {
                semantics.put(semantic, semid);
            } else {
                semantics.put(semantic, semid);
            }
        }

        public int countSemantics() {
            return semantics.size();
        }

    }

    private class SemanticEntity {
        private final String name;
        private Map<String, String> nodes;

        private SemanticEntity(String name) {
            this.name = name;
            nodes = new HashMap<String, String>();
        }

        public String getName() {
            return name;
        }

        public void addNode(String id) {
            nodes.put(id, "1");
        }

        public int countNodes() {
            return nodes.size();
        }

    }
}
