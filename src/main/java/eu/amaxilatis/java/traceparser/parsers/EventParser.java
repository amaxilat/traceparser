package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
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
 * Time: 2:10 PM
 */
public class EventParser extends AbstractParser implements Observer {

    private TraceFile file;
    private static final Logger log = Logger.getLogger(EventParser.class);
    private long duration;
    private int events[][];
    private int eventTypes;

    private String[] prefixes;

    public EventParser() {
        log.info("EventParser initialized");

    }

    public EventParser(String template) {


        String partitioner = "-";
        eventTypes = template.split(partitioner).length;

        prefixes = new String[eventTypes];
        final String[] templates = template.split(partitioner);


        for (int type = 0; type < eventTypes; type++) {
            log.info(templates[type]);
            String delimiter = ";";
            if (templates[type].contains(delimiter)) {
                prefixes[type] = templates[type].substring(0, templates[type].indexOf(delimiter));
            } else {
                prefixes[type] = templates[type];
            }
            log.info(prefixes[type]);
        }

        init();
    }

    private void init() {
        log.info("EventParser initialized");
    }

    //TODO: add multiple Events
    public EventParser(TraceFile f, String template) {

        //log.info("EventParser initialized");
        duration = f.duration();


        String partitioner = "-";
        eventTypes = template.split(partitioner).length;

        file = f;
        duration = f.duration() / 1000 + 1;
        events = new int[eventTypes][(int) duration];

        for (int type = 0; type < eventTypes; type++) {
            for (int j = 0; j < (int) duration; j++) {
                events[type][j] = 0;
            }
        }

        prefixes = new String[eventTypes];
        final String[] templates = template.split(partitioner);


        for (int type = 0; type < eventTypes; type++) {
            log.info(templates[type]);
            String delimiter = ";";
            if (templates[type].contains(delimiter)) {
                prefixes[type] = templates[type].substring(0, templates[type].indexOf(delimiter));
            } else {
                prefixes[type] = templates[type];
            }
            log.info(prefixes[type]);
        }

        init();
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        for (int type = 0; type < eventTypes; type++) {
            if (m.text().contains(prefixes[type])) {
                //log.info("Event@" + m.time() + ":" + m.urn());
                events[type][((int) ((m.time() - file.starttime()) / 1000))]++;
            }
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


    public XYSeries[] getSeries() {
        XYSeries[] series = new XYSeries[eventTypes];
        for (int type = 0; type < eventTypes; type++) {
            series[type] = new XYSeries("Events " + prefixes[type]);
            for (int i = 0; i < duration; i++) {
                series[type].add(i, events[type][i]);
            }
        }
        return series;
    }

    //TODO: add aggregate plots
    public XYSeries[] getSeries_aggregate() {
        XYSeries[] series = new XYSeries[eventTypes];
        for (int type = 0; type < eventTypes; type++) {
            if (prefixes[type].equals("CLL")) {
                series[type] = new XYSeries("Semantic Entity Changes");
            } else if (prefixes[type].equals("NB")) {
                series[type] = new XYSeries("Neighborhood Changes");
            } else {
                series[type] = new XYSeries("Events " + prefixes[type]);
            }
            for (int i = 0; i < duration; i++) {
                series[type].add(i, count_until(type, i));
            }
        }
        return series;
    }

    @Override
    public void setTraceFile(TraceFile file) {
        this.file = file;

        //log.info("EventParser initialized");
        duration = file.duration();


        duration = file.duration() / 1000 + 1;
        events = new int[eventTypes][(int) duration];

        for (int type = 0; type < eventTypes; type++) {
            for (int j = 0; j < (int) duration; j++) {
                events[type][j] = 0;
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
            sum += events[type][i];
        }
        return sum;
    }
}
