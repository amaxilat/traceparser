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
import java.util.Observable;
import java.util.Observer;

public class NeighborhoodParser extends AbstractParser implements Observer, ActionListener {

    private TraceFile file;
    private static final Logger log = Logger.getLogger(NeighborhoodParser.class);
    private HashMap<String, HashMap<String, Integer>> neighborsBidi = new HashMap();

    private XYSeries[] series;

    private String prefix_uni;
    private String prefix_lost;
    private String prefix_bidi;
    private String prefix_drop;

    private String delimiter;
    private final JButton plotbutton;

    private final JTextField delimitertextfield;
    private final JTextField nbtextfield;
    private final JTextField losttextfield;
    private final JTextField biditextfield;
    private final JTextField droptextfield;
    private final TextField plotTitle;
    private final TextField yLabel;
    private final TextField xLabel;
    public static final String Name = "Neighborhood Parser";
    private JTabbedPane tabbedPane;
    private JButton removeButton;


    public NeighborhoodParser(JTabbedPane jTabbedPane1) {
        this.tabbedPane = jTabbedPane1;
        init();

        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel(Name), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        plotbutton = new JButton(super.PLOT);
        plotbutton.addActionListener(this);
        removeButton = new JButton(super.REMOVE);
        removeButton.addActionListener(this);


        delimitertextfield = new JTextField(delimiter);
        nbtextfield = new JTextField(prefix_uni);
        biditextfield = new JTextField(prefix_bidi);
        droptextfield = new JTextField(prefix_drop);
        losttextfield = new JTextField(prefix_lost);

        leftmainpanel.add(new CouplePanel(new JLabel("delimiter"), delimitertextfield));
        leftmainpanel.add(new CouplePanel(new JLabel("Uni prefix"), nbtextfield));
        leftmainpanel.add(new CouplePanel(new JLabel("Bidi prefix"), biditextfield));
        leftmainpanel.add(new CouplePanel(new JLabel("Drop prefix"), droptextfield));
        leftmainpanel.add(new CouplePanel(new JLabel("Lost prefix"), losttextfield));


        rightmainpanel.add(new CouplePanel(plotbutton, removeButton));

        plotTitle = new TextField("Neighborhood Statistics");
        rightmainpanel.add(new CouplePanel(new JLabel("Plot title:"), plotTitle));
        xLabel = new TextField("getTime in sec");
        rightmainpanel.add(new CouplePanel(new JLabel("X axis Label:"), xLabel));
        yLabel = new TextField("# of Nodes");
        rightmainpanel.add(new CouplePanel(new JLabel("Y axis Label:"), yLabel));

        reset();


    }


    void setTemplates(String prefix_uni, String prefix_lost, String prefix_bidi, String prefix_drop) {
        this.prefix_uni = prefix_uni;
        this.prefix_lost = prefix_lost;
        this.prefix_drop = prefix_drop;
        this.prefix_bidi = prefix_bidi;
    }


    public void setTraceFile(TraceFile file) {
        this.file = file;
    }

    void init() {
        setDelimiter(";");
        setTemplates("NB", "NBL", "NBB", "NBD");
    }

    ChartPanel getPlot() {
        return getPlot(plotTitle.getText(), xLabel.getText(), yLabel.getText());
    }

    ChartPanel getPlot(String title, String xlabel, String ylabel) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] clustersSeries;
        clustersSeries = getSeries_aggregate();

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

    XYSeries[] getSeries() {
        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }

    XYSeries[] getSeries_aggregate() {
        return getSeries();
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (!NodeSelectorPanel.isSelected(m.getUrn())) return;

        if (m.getText().startsWith(prefix_uni)) {
//            LOGGER.info("Neighbor@" + m.getTime() + ":" + m.getUrn());
            final String target = m.getText().split(delimiter)[1];
            if ((m.getText().contains(prefix_bidi))) {

                if (neighborsBidi.containsKey(m.getUrn())) {
                    HashMap<String, Integer> tmp = neighborsBidi.get(m.getUrn());
                    tmp.put(target, 1);
                    neighborsBidi.put(m.getUrn(), tmp);
                    log.debug(m.getText());
                    log.debug(m.getUrn() + " nb size: " + tmp.size());
                } else {
                    HashMap tmp = new HashMap<String, Integer>();
                    tmp.put(target, 1);
                    neighborsBidi.put(m.getUrn(), tmp);
                    log.debug(m.getText());
                    log.debug(m.getUrn() + " nb size: " + tmp.size());
                }
            } else if ((m.getText().contains(prefix_drop)) || (m.getText().contains(prefix_lost))) {
                if (neighborsBidi.containsKey(m.getUrn())) {
                    HashMap<String, Integer> tmp = neighborsBidi.get(m.getUrn());
                    tmp.remove(target);
                    neighborsBidi.put(m.getUrn(), tmp);
                    log.debug(m.getText());
                    log.debug(m.getUrn() + " nb size: " + tmp.size());
                }

            }

            series[0].addOrUpdate(((int) ((m.getTime() - file.getStartTime()) / 1000)), get_avg_neighbors());
            series[1].addOrUpdate(((int) ((m.getTime() - file.getStartTime()) / 1000)), get_min_neighbors());
            series[2].addOrUpdate(((int) ((m.getTime() - file.getStartTime()) / 1000)), get_max_neighbors());

        }
    }

    private double get_avg_neighbors() {
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

    private double get_max_neighbors() {
        int max = 0;
        for (String urn : neighborsBidi.keySet()) {
            if (max < neighborsBidi.get(urn).size()) {
                max = neighborsBidi.get(urn).size();
            }
        }
        return max;
    }

    private double get_min_neighbors() {
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

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotbutton)) {
            reset();
            log.info("|=== parsing tracefile: " + file.getFilename() + "...");
            TraceReader a = new TraceReader(file);
            a.addObserver(this);
            a.run();
            log.info("|--- done parsing!");
            log.info("|=== generating plot...");
            JFrame jnew = new JFrame();
            jnew.add(getPlot());
            jnew.pack();
            jnew.setVisible(true);
            log.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(removeButton)) {
            tabbedPane.remove(this);
        }
    }

    private void reset() {
        setDelimiter(delimitertextfield.getText());
        setTemplates(nbtextfield.getText(), losttextfield.getText(), biditextfield.getText(), droptextfield.getText());

        neighborsBidi = new HashMap();
        series = new XYSeries[3];
        series[0] = new XYSeries("Avg Neighbors");
        series[1] = new XYSeries("Min Neighbors");
        series[2] = new XYSeries("Max Neighbors");
        log.info("NeighborhoodParser initialized");
    }

    private void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

}

