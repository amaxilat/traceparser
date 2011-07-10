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
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventParser implements Observer, AbstractParser {

    TraceFile file;
    private Logger log;
    private long duration;
    private int events[];
    private XYSeries[] series;

    private String prefix;
    private String delimiter = ";";

    public EventParser() {
        log = TraceParserApp.log;
        log.info("EventParser initialized");

    }

    public EventParser(TraceFile f, String template) {

        log = TraceParserApp.log;
        //log.info("EventParser initialized");
        duration = f.duration();

        file = f;
        duration = f.duration() / 1000;
        events = new int[(int) duration];

        for (int j = 0; j < (int) duration; j++) {
            events[j] = 0;
        }

        prefix = template.substring(0,template.indexOf(delimiter));

        log.info("EventParser initialized");
    }


    public void update() {
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (m.text().startsWith(prefix)) {
            log.info("Event@" + m.time() + ":" + m.urn());
            events[((int) ((m.time() - file.starttime()) / 1000))]++;
        }
    }

    public ChartPanel getPlot(boolean has_title) {
        String title = "";
        if (has_title) {
            title = "Messages over time";
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] messageTypes = getSeries();
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


    public XYSeries[] getSeries() {
        XYSeries[] series = new XYSeries[1];
        series[0] = new XYSeries("Events " + prefix);
        for (int i = 0; i < duration; i++) {
            series[0].add(i, events[i]);
        }
        return series;
    }
}
