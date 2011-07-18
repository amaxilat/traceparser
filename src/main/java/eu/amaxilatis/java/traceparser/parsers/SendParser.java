package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceParserApp;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/2/11
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendParser implements Observer, AbstractParser {

    TraceFile file;
    private Logger log;
    private long duration;
    private int messages[][];
    private XYSeries[] series;

    private String prefix;
    private String delimiter = ";";
    private int sender = 1;
    private int type = 2;
    private int destination = 3;


    public SendParser() {
        log = TraceParserApp.log;
        log.info("SendParser initialized");

    }

    public SendParser(TraceFile f, String template) {

        log = TraceParserApp.log;

        file = f;
        duration = f.duration() / 1000;
        messages = new int[255][(int) duration];
        for (int i = 0; i < 255; i++) {
            for (int j = 0; j < (int) duration; j++) {
                messages[i][j] = 0;
            }
        }

        final String[] parts = template.split(delimiter);
        prefix = parts[0];
        if (parts[1].equals("%s"))
            sender = 1;
        else if (parts[1].equals("%t"))
            type = 1;
        else if (parts[1].equals("%d"))
            destination = 1;

        if (parts[2].equals("%s"))
            sender = 2;
        else if (parts[2].equals("%t"))
            type = 2;
        else if (parts[2].equals("%d"))
            destination = 2;

        if (parts[3].equals("%s"))
            sender = 3;
        else if (parts[3].equals("%t"))
            type = 3;
        else if (parts[3].equals("%d"))
            destination = 3;


        log.info("SendParser initialized");

    }

    public void update() {
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (m.text().startsWith(prefix)) {
            log.info("Send@" + m.time() + ":" + m.urn());
            final String[] mess = m.text().split(delimiter);
            messages[Integer.parseInt(mess[type])][((int) ((m.time() - file.starttime()) / 1000))]++;
        }
    }


    public ChartPanel getPlot(boolean has_title, boolean aggregate) {
        String title = "";
        if (has_title) {
            title = "Messages over time";
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] messageTypes = null;
        if (aggregate) {
            messageTypes = getSeries_aggregate();
        } else {
            messageTypes = getSeries();
        }

        for (int i = 0; i < messageTypes.length; i++) {
            dataset.addSeries(messageTypes[i]);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "xlabel",
                "ylabel",
                dataset, PlotOrientation.VERTICAL, true, true, false);


        return new ChartPanel(chart);
    }


    boolean exists(int type) {
        for (int i = 0; i < duration; i++) {
            if (messages[type][i] != 0) {
                return true;
            }
        }
        return false;
    }


    public XYSeries[] getSeries() {

        int total = 0;
        for (int i = 0; i < 255; i++) {
            if (exists(i)) {
                total++;
            }
        }

        XYSeries[] series = new XYSeries[total];
        int ctype = 0;
        for (int types = 0; types < 255; types++) {
            if (exists(types)) {
                series[ctype] = new XYSeries("Mes. " + types);
                for (int i = 0; i < duration; i++) {
                    series[ctype].add(i, messages[types][i]);
                }
                ctype++;
            }
        }
        return series;
    }


    public XYSeries[] getSeries_aggregate() {

        int total = 0;
        for (int i = 0; i < 255; i++) {
            if (exists(i)) {
                total++;
            }
        }
        //TODO: add aggregate plots
        XYSeries[] series = new XYSeries[total];
        int ctype = 0;
        for (int types = 0; types < 255; types++) {
            if (exists(types)) {
                series[ctype] = new XYSeries("Mes. " + types);
                for (int i = 0; i < duration; i++) {
                    series[ctype].add(i, count_until(types, i));
                }
                ctype++;
            }
        }
        return series;

    }

    private int count_until(int type, int time_until) {
        int sum = 0;
        for (int i = 0; i <= time_until; i++) {
            sum += messages[type][i];
        }
        return sum;
    }
}
