package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceReader;
import eu.amaxilatis.java.traceparser.panels.couplePanel;
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

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/19/11
 * Time: 6:39 PM
 */
public class NeighborhoodParser extends AbstractParser implements Observer, ActionListener {

    private TraceFile file;
    private static final Logger log = Logger.getLogger(NeighborhoodParser.class);
    private HashMap<String, Integer> neighbors;


    private XYSeries[] series;

    private String prefix_uni;
    private String prefix_lost;
    private String prefix_bidi;
    private String prefix_drop;

    private String delimiter;
    private JButton plotbutton;
    private JButton updatebutton;
    private JTextField delimitertextfield;
    private JTextField nbtextfield;
    private JTextField losttextfield;
    private JTextField biditextfield;
    private JTextField droptextfield;


    public NeighborhoodParser() {
        init();

        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel("NeighborhoodParser"), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        plotbutton = new JButton("plot");
        plotbutton.addActionListener(this);
        updatebutton = new JButton("reload configuration");
        updatebutton.addActionListener(this);

        delimitertextfield = new JTextField(delimiter);
        nbtextfield = new JTextField(prefix_uni);
        biditextfield = new JTextField(prefix_bidi);
        droptextfield = new JTextField(prefix_drop);
        losttextfield = new JTextField(prefix_lost);

        leftmainpanel.add(new couplePanel(new JLabel("delimiter"), delimitertextfield));
        leftmainpanel.add(new couplePanel(new JLabel("Uni prefix"), nbtextfield));
        leftmainpanel.add(new couplePanel(new JLabel("Bidi prefix"), biditextfield));
        leftmainpanel.add(new couplePanel(new JLabel("Drop prefix"), droptextfield));
        leftmainpanel.add(new couplePanel(new JLabel("Lost prefix"), losttextfield));

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


    public void setTemplates(String prefix_uni, String prefix_lost, String prefix_bidi, String prefix_drop) {
        this.prefix_uni = prefix_uni;
        this.prefix_lost = prefix_lost;
        this.prefix_drop = prefix_drop;
        this.prefix_bidi = prefix_bidi;
    }


    public void setTraceFile(TraceFile file) {
        this.file = file;
    }

    public void init() {
        setDelimiter(";");
        setTemplates("NB", "NBL", "NBB", "NBD");
        reset();
    }

    public ChartPanel getPlot() {
        return getPlot(false, true, "", "", "");
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
        chart.setBackgroundPaint(Color.white);

        return new ChartPanel(chart);
    }

    public XYSeries[] getSeries() {


        return series;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public XYSeries[] getSeries_aggregate() {
        return getSeries();
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (m.text().startsWith(prefix_uni)) {
            log.info("Neighbor@" + m.time() + ":" + m.urn());
            int nb_change = 0;
//            if ((m.text().contains(prefix + ";")) | (m.text().contains(prefix + "B;"))) {
            if ((m.text().contains(prefix_bidi))) {
                //add neighbor
                nb_change = +1;
            } else if ((m.text().contains(prefix_drop))) {
                nb_change = -1;
            }
            if (neighbors.containsKey(m.urn())) {
                neighbors.put(m.urn(), neighbors.get(m.urn()) + nb_change);
            } else {
                neighbors.put(m.urn(), 1);
            }
            //log.info(m.text()+ ":: "+nb_change);
            //log.info(get_avg_neighbors());
            series[0].addOrUpdate(((int) ((m.time() - file.starttime()) / 1000)), get_avg_neighbors());
            series[1].addOrUpdate(((int) ((m.time() - file.starttime()) / 1000)), get_min_neighbors());
            series[2].addOrUpdate(((int) ((m.time() - file.starttime()) / 1000)), get_max_neighbors());
        }
    }

    private double get_avg_neighbors() {
        int count = 0;
        int sum = 0;
        for (int val : neighbors.values()) {
            sum += val;
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
        for (int val : neighbors.values()) {
            if (max < val) {
                max = val;
            }
        }
        return max;
    }

    private double get_min_neighbors() {
        int min = 10000;
        for (int val : neighbors.values()) {
            if (min > val) {
                min = val;
            }
        }
        return min;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotbutton)) {
            reset();
            log.info("|=== parsing tracefile: " + file.filename() + "...");
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
        } else if (actionEvent.getSource().equals(updatebutton)) {
            setDelimiter(delimitertextfield.getText());
            setTemplates(nbtextfield.getText(), losttextfield.getText(), biditextfield.getText(), droptextfield.getText());

        }
    }

    private void reset() {
        neighbors = new HashMap<String, Integer>();
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

