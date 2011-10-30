package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.ChartFormater;
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
import org.omg.PortableServer.POAPackage.WrongAdapter;

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
public class ClustersParser extends AbstractParser implements Observer, ActionListener {

    private TraceFile file;
    private static final Logger log = Logger.getLogger(ClustersParser.class);
    private long duration;
    private HashMap<String, String>[] clusters;


    private String template;
    private String prefix;
    private String delimiter = ";";
    private String[] parts;
    private JButton plotbutton;
    private JButton updatebutton;
    private JTextField delimitertextfield;
    private JTextField templatetextfield;
    private int pcluster;
    private int pid;
    private int ptype;
    public static String Name = "Clusters Parser";


    public ClustersParser() {

        init();

        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel(Name), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        plotbutton = new JButton("plot");
        plotbutton.addActionListener(this);
        updatebutton = new JButton("reload configuration");
        updatebutton.addActionListener(this);

        delimitertextfield = new JTextField(delimiter);
        templatetextfield = new JTextField(template);

        leftmainpanel.add(new couplePanel(new JLabel("delimiter"), delimitertextfield));
        leftmainpanel.add(new couplePanel(new JLabel("Template"), templatetextfield));

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

    public ClustersParser(String template) {

        //clusters = new int[f.nodesize()][(int) duration];

        parts = template.split(delimiter);


        init();
    }

    private void init() {

        setTemplate("CLP;ID;TYPE;CLUSTER");

        pcluster = 0;
        pid = 0;
        ptype = 0;

        if (parts.length < 4) {
            log.error("invalid argument!!!");
        } else {
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("ID")) {
                    pid = i;
                } else if (parts[i].equals("CLUSTER")) {
                    pcluster = i;
                } else if (parts[i].equals("TYPE")) {
                    ptype = i;
                }
            }
        }


        log.info("ClustersParser initialized");
    }

//    public ClustersParser(TraceFile f, String template) {
//        //log.info("EventParser initialized");
//        duration = f.duration();
//
//
//        file = f;
//        int duration = (int) (f.duration() / 1000 + 1);
//        clusters = new HashMap[(int) duration];
//
//        //clusters = new int[f.nodesize()][(int) duration];
//
//        for (int i = 0; i < duration; i++) {
//            clusters[i] = new HashMap<String, String>();
//            clusters[i].clear();
//        }
//
//        parts = template.split(delimiter);
//
//        init();
//    }

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

        log.debug(duration);
        for (int i = 0; i < duration; i++) {
            int cluster_count = 0;
            int simple_count = 0;
            log.info(clusters[i].keySet().size());
            for (String key : clusters[i].keySet()) {

                if (clusters[i].get(key).equals(key)) {
                    log.info(key + " - " + clusters[i].get(key) + " N");
                    cluster_count++;
                } else {
                    log.info(key + " - " + clusters[i].get(key));
                    simple_count++;
                }
            }


            series[0].add(i, cluster_count);
            if (cluster_count > 0) {
                //log.info("Clusters : " + cluster_count + " clSize : " + (simple_count + cluster_count) / cluster_count);
                series[1].add(i, (simple_count + cluster_count) / cluster_count);
            } else {
                //log.info("Clusters : " + cluster_count + " clSize : 0");
                series[1].add(i, 0);
            }
            log.debug("setting " + i);
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
        log.debug(m.text());
        if (m.text().startsWith(prefix)) {
            log.info("Cluster@" + m.time() + ":" + m.urn());
            final String[] mess = m.text().split(delimiter);
            set_cluster(mess[pid], mess[pcluster], ((int) ((m.time() - file.starttime()) / 1000)));
        }
    }

    private void set_cluster(String node, String clust, int time) {
        log.debug(node + "-" + clust + "@" + time);
        for (int i = time; i < duration - 1; i++) {
            clusters[i].put(node, clust);
        }
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
            setTemplate(templatetextfield.getText());
        }
    }

    private void setDelimiter(String text) {
        delimiter = text;
    }

    private void reset() {
        duration = (int) (file.duration() / 1000 + 1);
        clusters = new HashMap[(int) duration];

        //clusters = new int[f.nodesize()][(int) duration];

        for (int i = 0; i < duration; i++) {
            clusters[i] = new HashMap<String, String>();
            clusters[i].clear();
        }

        init();
    }
}
