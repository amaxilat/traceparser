package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceReader;
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
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/2/11
 * Time: 2:10 PM
 */
public class EventParser extends GenericParser implements Observer, ActionListener {

    public static final String NAME = "Events Parser";
    private static final Logger LOGGER = LoggerFactory.getLogger(EventParser.class);
    private static final String PARTITIONER = ",";
    private transient long duration;
    private transient int events[][];
    private transient int eventTypes;

    private final transient JButton plot;
    private final transient JTextField partitionerTf;
    private final transient JTextField templatesTf;
    private final transient JTabbedPane tabbedPane;
    private final transient TextField plotTitle;
    private final transient TextField xLabel;
    private final transient TextField yLabel;
    private final transient JButton remove;

    private transient String[] prefixes;
    private transient String templates = "NB,CLL";

    /**
     * @param jTabbedPane1
     */
    public EventParser(final JTabbedPane jTabbedPane1) {
        super(NAME);
        this.tabbedPane = jTabbedPane1;

        plot = new JButton(super.PLOT);
        plot.addActionListener(this);
        remove = new JButton(super.REMOVE);
        remove.addActionListener(this);
        addRight(plot, remove);


        partitionerTf = new JTextField(PARTITIONER);
        templatesTf = new JTextField(templates);
        addLeft(new JLabel("partitioner"), partitionerTf);
        addLeft(new JLabel("templates"), templatesTf);


        plotTitle = new TextField("Event Statistics");
        addRight(new JLabel("Plot title:"), plotTitle);
        xLabel = new TextField("getTime in sec");
        addRight(new JLabel("X axis Label:"), xLabel);
        yLabel = new TextField("# of Events");
        addRight(new JLabel("Y axis Label:"), yLabel);

        init();

    }

    /**
     *
     */
    private void init() {
        LOGGER.info("EventParser initialized");
    }

    /**
     * @param observable
     * @param obj
     */
    public void update(final Observable observable, final Object obj) {
        final TraceMessage message = (TraceMessage) obj;
        if (NodeSelectorPanel.isSelected(message.getUrn())) {

            for (int type = 0; type < eventTypes; type++) {
                if (message.getText().contains(prefixes[type])) {
                    //LOGGER.info("Event@" + message.getTime() + ":" + message.getUrn());
                    events[type][((int) ((message.getTime() - TraceFile.getInstance().getStartTime()) / 1000))]++;
                }
            }
        }
    }

    /**
     * @param aggregate
     * @return
     */
    public ChartPanel getPlot(final boolean aggregate) {

        final XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] messageTypes;
        if (aggregate) {
            messageTypes = getSeriesAggregate();
        } else {
            messageTypes = getSeries();
        }
        for (XYSeries messageType : messageTypes) {
            dataset.addSeries(messageType);
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                plotTitle.getText(),
                xLabel.getText(),
                yLabel.getText(),
                dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.white);
        return new ChartPanel(chart);
    }

    /**
     * @return
     */
    public ChartPanel getPlot() {
        return getPlot(true);
    }

    /**
     * @return
     */
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

    /**
     * @return
     */
    public XYSeries[] getSeriesAggregate() {
        XYSeries[] series = new XYSeries[eventTypes];
        for (int type = 0; type < eventTypes; type++) {
            if ("CLL".equals(prefixes[type])) {
                series[type] = new XYSeries("Semantic Entity Changes");
            } else if ("NB".equals(prefixes[type])) {
                series[type] = new XYSeries("Neighborhood Changes");
            } else {
                series[type] = new XYSeries("Events " + prefixes[type]);
            }
            for (int i = 0; i < duration; i++) {
                series[type].add(i, countUntil(type, i));
            }
        }
        return series;
    }

    /**
     * @param type
     * @param timeUntil
     * @return
     */
    private int countUntil(final int type, final int timeUntil) {
        int sum = 0;
        for (int i = 0; i <= timeUntil; i++) {
            sum += events[type][i];
        }
        return sum;
    }

    /**
     * @param actionEvent
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plot)) {
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
        } else if (actionEvent.getSource().equals(remove)) {
            tabbedPane.remove(this);
        }
    }

    /**
     *
     */
    private void reset() {
        templates = templatesTf.getText();
        prefixes = templates.split(partitionerTf.getText());
        eventTypes = prefixes.length;

        duration = TraceFile.getInstance().getDuration() / 1000 + 1;
        LOGGER.info(eventTypes + " dur" + duration);
        events = new int[eventTypes][(int) duration];

        for (int type = 0; type < eventTypes; type++) {
            for (int j = 0; j < (int) duration; j++) {
                events[type][j] = 0;
            }
        }
    }
}
