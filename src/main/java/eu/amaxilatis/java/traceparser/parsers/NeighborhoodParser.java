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

public class NeighborhoodParser extends AbstractParser implements Observer, ActionListener {

    private static final Logger LOGGER = Logger.getLogger(NeighborhoodParser.class);
    public static final String NAME = "Neighborhood Parser";

    private final JTabbedPane tabbedPane;
    private final JButton plotbutton;
    private final JButton removeButton;
    private final JTextField delimiterTf;
    private final JTextField nbTf;
    private final JTextField lostTf;
    private final JTextField bidiTf;
    private final JTextField dropTf;
    private final JTextField plotTitle;
    private final JTextField yLabel;
    private final JTextField xLabel;

    private TraceFile file;
    private String delimiter;
    private Map<String, HashMap<String, Integer>> neighborsBidi;
    private String prefixUni;
    private String prefixLost;
    private String prefixBidi;
    private String prefixDrop;

    private XYSeries[] series;


    public NeighborhoodParser(final JTabbedPane jTabbedPane1) {
        tabbedPane = jTabbedPane1;
        init();

        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel(NAME), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        plotbutton = new JButton(super.PLOT);
        plotbutton.addActionListener(this);
        removeButton = new JButton(super.REMOVE);
        removeButton.addActionListener(this);


        delimiterTf = new JTextField(delimiter);
        nbTf = new JTextField(prefixUni);
        bidiTf = new JTextField(prefixBidi);
        dropTf = new JTextField(prefixDrop);
        lostTf = new JTextField(prefixLost);

        leftmainpanel.add(new CouplePanel(new JLabel("delimiter"), delimiterTf));
        leftmainpanel.add(new CouplePanel(new JLabel("Uni prefix"), nbTf));
        leftmainpanel.add(new CouplePanel(new JLabel("Bidi prefix"), bidiTf));
        leftmainpanel.add(new CouplePanel(new JLabel("Drop prefix"), dropTf));
        leftmainpanel.add(new CouplePanel(new JLabel("Lost prefix"), lostTf));


        rightmainpanel.add(new CouplePanel(plotbutton, removeButton));

        plotTitle = new JTextField("Neighborhood Statistics");
        rightmainpanel.add(new CouplePanel(new JLabel("Plot title:"), plotTitle));
        xLabel = new JTextField("getTime in sec");
        rightmainpanel.add(new CouplePanel(new JLabel("X axis Label:"), xLabel));
        yLabel = new JTextField("# of Nodes");
        rightmainpanel.add(new CouplePanel(new JLabel("Y axis Label:"), yLabel));

        reset();


    }


    void setTemplates(final String prefix_uni, final String prefix_lost, final String prefix_bidi, final String prefix_drop) {
        this.prefixUni = prefix_uni;
        this.prefixLost = prefix_lost;
        this.prefixDrop = prefix_drop;
        this.prefixBidi = prefix_bidi;
    }


    public void setTraceFile(final TraceFile file) {
        this.file = file;
    }

    private void init() {
        setDelimiter(";");
        setTemplates("NB", "NBL", "NBB", "NBD");
    }

    private ChartPanel getPlot() {
        return getPlot(plotTitle.getText(), xLabel.getText(), yLabel.getText());
    }

    private ChartPanel getPlot(final String title, final String xlabel, final String ylabel) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] clustersSeries;
        clustersSeries = getAggregatedSeries();

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

    private XYSeries[] getSeries() {
        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private XYSeries[] getAggregatedSeries() {
        return getSeries();
    }

    public void update(final Observable observable, final Object object) {
        final TraceMessage message = (TraceMessage) object;
        if (NodeSelectorPanel.isSelected(message.getUrn())) {


            if (message.getText().startsWith(prefixUni)) {
//            LOGGER.info("Neighbor@" + message.getTime() + ":" + message.getUrn());
                final String target = message.getText().split(delimiter)[1];
                if ((message.getText().contains(prefixBidi))) {

                    if (neighborsBidi.containsKey(message.getUrn())) {
                        final HashMap<String, Integer> tmp = neighborsBidi.get(message.getUrn());
                        tmp.put(target, 1);
                        neighborsBidi.put(message.getUrn(), tmp);
                        LOGGER.debug(message.getText());
                        LOGGER.debug(message.getUrn() + " nb size: " + tmp.size());
                    } else {
                        HashMap tmp = new HashMap<String, Integer>();
                        tmp.put(target, 1);
                        neighborsBidi.put(message.getUrn(), tmp);
                        LOGGER.debug(message.getText());
                        LOGGER.debug(message.getUrn() + " nb size: " + tmp.size());
                    }
                } else if ((message.getText().contains(prefixDrop)) || (message.getText().contains(prefixLost))) {
                    if (neighborsBidi.containsKey(message.getUrn())) {
                        HashMap<String, Integer> tmp = neighborsBidi.get(message.getUrn());
                        tmp.remove(target);
                        neighborsBidi.put(message.getUrn(), tmp);
                        LOGGER.debug(message.getText());
                        LOGGER.debug(message.getUrn() + " nb size: " + tmp.size());
                    }

                }

                series[0].addOrUpdate(((int) ((message.getTime() - file.getStartTime()) / 1000)), getAvgNeighbors());
                series[1].addOrUpdate(((int) ((message.getTime() - file.getStartTime()) / 1000)), getMinNeighbors());
                series[2].addOrUpdate(((int) ((message.getTime() - file.getStartTime()) / 1000)), getMaxNeighbors());

            }
        }
    }

    private double getAvgNeighbors() {
        int count = 0;
        int sum = 0;

        for (String urn : neighborsBidi.keySet()) {
            sum += neighborsBidi.get(urn).size();
            count++;
        }
        if (count > 0)
            return (double) sum / count;
        else {
            return 0;
        }
    }

    private double getMaxNeighbors() {
        int max = 0;
        for (String urn : neighborsBidi.keySet()) {
            if (max < neighborsBidi.get(urn).size()) {
                max = neighborsBidi.get(urn).size();
            }
        }
        return max;
    }

    private double getMinNeighbors() {
        int min = 0;
        for (String urn : neighborsBidi.keySet()) {

            if (min > neighborsBidi.get(urn).size()) {
                min = neighborsBidi.get(urn).size();
            }
            if (min == 0) {
                min = neighborsBidi.get(urn).size();
            }
        }
        return min;
    }

    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotbutton)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + file.getFilename() + "...");
            TraceReader traceReader = new TraceReader(file);
            traceReader.addObserver(this);
            traceReader.run();
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

    private void reset() {
        setDelimiter(delimiterTf.getText());
        setTemplates(nbTf.getText(), lostTf.getText(), bidiTf.getText(), dropTf.getText());

        neighborsBidi = new HashMap();
        series = new XYSeries[3];
        series[0] = new XYSeries("Avg Neighbors");
        series[1] = new XYSeries("Min Neighbors");
        series[2] = new XYSeries("Max Neighbors");
        LOGGER.info("NeighborhoodParser initialized");
    }

    private void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

}

