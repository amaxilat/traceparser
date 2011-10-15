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

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/2/11
 * Time: 1:19 PM
 */
public class SendParser extends AbstractParser implements Observer {

    private TraceFile file;
    private static final Logger log = Logger.getLogger(SendParser.class);
    private long duration;
    private int messages[][];

    private String prefix;
    private final String delimiter = ";";
    private int type = 2;
    private String[] parts;


    private SendParser() {

        log.info("SendParser initialized");

    }

    public SendParser(String template) {

        parts = template.split(delimiter);
        init();

    }

    private void init() {
        prefix = parts[0];
        int destination = 3;
        int sender = 1;
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

    public SendParser(TraceFile f, String template) {

        file = f;
        duration = f.duration() / 1000 + 1;
        messages = new int[255][(int) duration];
        for (int i = 0; i < 255; i++) {
            for (int j = 0; j < (int) duration; j++) {
                messages[i][j] = 0;
            }
        }

        parts = template.split(delimiter);
        init();
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (m.text().startsWith(prefix)) {
            //log.info("Send@" + m.time() + ":" + m.urn());
            final String[] mess = m.text().split(delimiter);
            //log.info(((int) ((m.time() - file.starttime()) / 1000)));
            messages[Integer.parseInt(mess[type])][((int) ((m.time() - file.starttime()) / 1000))]++;
        }
    }


    public ChartPanel getPlot(boolean has_title, boolean aggregate, String title, String xlabel, String ylabel) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] messageTypes;
        if (aggregate) {
            messageTypes = getSeries_aggregate();
        } else {
            messageTypes = getSeries();
        }

        for (XYSeries messageType : messageTypes) {
            dataset.addSeries(messageType);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xlabel,
                ylabel,
                dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.white);

        return new ChartPanel(chart);
    }

    @Override
    public ChartPanel getPlot() {
        return getPlot(false, true, "", "", "");
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

    @Override
    public void setTraceFile(TraceFile file) {
        this.file = file;

        duration = file.duration() / 1000 + 1;
        messages = new int[255][(int) duration];
        for (int i = 0; i < 255; i++) {
            for (int j = 0; j < (int) duration; j++) {
                messages[i][j] = 0;
            }
        }
    }

    @Override
    public void setTemplate(String template) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTemplate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private int count_until(int type, int time_until) {
        int sum = 0;
        for (int i = 0; i <= time_until; i++) {
            sum += messages[type][i];
        }
        return sum;
    }
}
