package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.ChartFormatter;
import eu.amaxilatis.java.traceparser.traces.TraceFile;
import eu.amaxilatis.java.traceparser.traces.AbstractTraceMessage;
import eu.amaxilatis.java.traceparser.traces.TraceReader;
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

/**
 *
 */
public class ClusterOverlapParser extends GenericParser implements Observer, ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClustersParser.class);
    public static final String NAME = "ClusterOverlap Parser";
    private static final String PLOT_TITLE = "Cluster Overlap Statistics";
    private static final String X_LABEL = "time in sec";
    private static final String Y_LABEL = "# of Clusters";

    private final transient JButton plotButton;
    private final transient JButton removeButton;
    private final transient JTextField delimiterTf;
    private final transient JTextField templateTf;
    private final transient JTabbedPane tabbedPane;
    private final transient JTextField plotTitleTf;
    private final transient JTextField xLabelTf;
    private final transient JTextField yLabelTf;
    private final transient JTextField clTypeTf;

    private transient Map<String, Node> nodes;
    private transient Map<String, SemanticEntity> semanticEntityMap;
    private transient int pid, pcluster, pparent, ptname, ptid;
    private transient String template;
    private transient String prefix;
    private final transient String clType = "NAME-ID";
    private transient String delimiter = ";";
    private transient String[] parts;
    private final transient String clTypeDelimiter = "-";
    private transient int prev = 0;

    XYSeries avgClusters = new XYSeries("avg SE per node");

    /**
     * @param jTabbedPane1
     */
    public ClusterOverlapParser(final JTabbedPane jTabbedPane1) {
        super(NAME);
        this.tabbedPane = jTabbedPane1;

        template = "CLL;ID;CLUSTER;PARENT";
        parts = template.split(delimiter);
        prefix = template.substring(0, template.indexOf(delimiter));

        this.setLayout(new BorderLayout());

        delimiterTf = new JTextField(delimiter);
        templateTf = new JTextField(template);
        clTypeTf = new JTextField(clType);

        addLeft(new JLabel("delimiter"), delimiterTf);
        addLeft(new JLabel("Template"), templateTf);
        addLeft(new JLabel("Cluster Template"), clTypeTf);


        plotButton = new JButton(super.PLOT);
        plotButton.addActionListener(this);
        removeButton = new JButton(super.REMOVE);
        removeButton.addActionListener(this);
        addRight(plotButton, removeButton);


        plotTitleTf = new JTextField(PLOT_TITLE);
        addRight(new JLabel("Plot title:"), plotTitleTf);
        xLabelTf = new JTextField(X_LABEL);
        addRight(new JLabel("X axis Label:"), xLabelTf);
        yLabelTf = new JTextField(Y_LABEL);
        addRight(new JLabel("Y axis Label:"), yLabelTf);


        init();
    }

    /**
     *
     */
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

    /**
     * @param aggregate
     * @param title
     * @param xlabel
     * @param ylabel
     * @return
     */
    public ChartPanel getPlot(boolean aggregate, final String title, final String xlabel, final String ylabel) {
        final XYSeriesCollection dataSet = new XYSeriesCollection();
        XYSeries[] clustersSeries;

        clustersSeries = getSeries();


        for (XYSeries series : clustersSeries) {
            dataSet.addSeries(series);
        }


        final JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xlabel,
                ylabel,
                dataSet, PlotOrientation.VERTICAL, true, true, false);


        final JFreeChart chartTransformed = ChartFormatter.transformChart(chart);
        return new ChartPanel(chartTransformed);
    }

    /**
     * @return
     */
    public ChartPanel getPlot() {

        for (final String entry : semanticEntityMap.keySet()) {
            LOGGER.info("SE : " + entry + "contains :" + semanticEntityMap.get(entry).countNodes());
        }

        return getPlot(false, plotTitleTf.getText(), xLabelTf.getText(), yLabelTf.getText());
    }

    /**
     * @return
     */
    public XYSeries[] getSeries() {
        final XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(avgClusters);

        XYSeries[] series = new XYSeries[collection.getSeriesCount()];
        for (int i = 0; i < collection.getSeriesCount(); i++) {
            series[i] = collection.getSeries(i);
        }

        return series;
    }

    /**
     * @param template
     */
    void setTemplate(final String template) {
        parts = template.split(delimiter);
        prefix = template.substring(0, template.indexOf(delimiter));
        this.template = template;
    }

    /**
     * @param observable
     * @param object
     */
    public void update(final Observable observable, final Object object) {
        final AbstractTraceMessage message = (AbstractTraceMessage) object;
        if (NodeSelectorPanel.isSelected(message.getUrn())) {

            LOGGER.debug(message.getText());
            if (message.getText().startsWith(prefix)) {
//            LOGGER.debug("Cluster@" + message.getTime() + ":" + message.getUrn());
                final String[] mess = message.getText().split(delimiter);
                final int time = ((int) ((message.getTime() - TraceFile.getInstance().getStartTime()) / 1000));
                setCluster(mess[pid], mess[pcluster].split(clTypeDelimiter)[ptname], time);
                setSemanticEntity(mess[pcluster].split(clTypeDelimiter)[ptname], mess[pid], time);
            }
        }
    }

    /**
     * @param semanticEntity
     * @param id
     * @param time
     */
    private void setSemanticEntity(final String semanticEntity, final String id, final int time) {
        if (semanticEntityMap.containsKey(semanticEntity)) {
            semanticEntityMap.get(semanticEntity).addNode(id);
        } else {
            final SemanticEntity seEntityEntry = new SemanticEntity(semanticEntity);
            seEntityEntry.addNode(id);
            semanticEntityMap.put(semanticEntity, seEntityEntry);
        }
    }

    /**
     * @param node
     * @param cluster
     * @param time
     */
    private void setCluster(final String node, final String cluster, final int time) {
        Node tempNode;
        if (nodes.containsKey(node)) {
            tempNode = nodes.get(node);
            tempNode.setSemantic(cluster, "1");
        } else {
            tempNode = new Node(node);
            tempNode.setSemantic(cluster, "1");
        }
        nodes.put(node, tempNode);

        set2series(time);
        LOGGER.debug(node + "-" + tempNode.countSemantics() + "@" + time);
    }

    /**
     * @param time
     */
    private void set2series(final long time) {
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

    /**
     * @param actionEvent
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotButton)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + TraceFile.getInstance().getFilename() + "...");
            final TraceReader reader = new TraceReader();
            reader.addObserver(this);
            reader.run();
            LOGGER.info("|--- done parsing!");
            LOGGER.info("|=== generating plot...");
            final JFrame frame = new JFrame();
            frame.add(getPlot());
            frame.pack();
            frame.setVisible(true);
            LOGGER.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(removeButton)) {
            tabbedPane.remove(this);
        }
    }

    /**
     *
     */
    private void reset() {
        delimiter = delimiterTf.getText();
        template = templateTf.getText();


        nodes = new HashMap<String, Node>();
        semanticEntityMap = new HashMap<String, SemanticEntity>();
        init();
    }

    /**
     *
     */
    private class Node {
        private final transient String nodeId;
        private final transient Map<String, String> semantics;

        /**
         * @param nodeId
         */
        public Node(String nodeId) {
            this.nodeId = nodeId;
            semantics = new HashMap<String, String>();
        }

        /**
         * @return
         */
        public String getNodeId() {
            return nodeId;
        }

        /**
         * @param semantic
         * @param semid
         */
        public void setSemantic(final String semantic, final String semid) {
            if (semantics.containsKey(semantic)) {
                semantics.put(semantic, semid);
            } else {
                semantics.put(semantic, semid);
            }
        }

        /**
         * @return
         */
        public int countSemantics() {
            return semantics.size();
        }

    }

    /**
     *
     */
    private class SemanticEntity {
        private final transient String name;
        private final transient Map<String, String> nodes;

        /**
         * @param name
         */
        public SemanticEntity(final String name) {
            this.name = name;
            nodes = new HashMap<String, String>();
        }

        /**
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * @param node
         */
        public void addNode(final String node) {
            nodes.put(node, "1");
        }

        /**
         * @return
         */
        public int countNodes() {
            return nodes.size();
        }

    }
}
