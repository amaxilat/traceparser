package eu.amaxilatis.java.traceparser.parsers;

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
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/2/11
 * Time: 2:10 PM
 */
public class EventParser extends AbstractParser implements Observer, ActionListener {

    public static final String NAME = "Events Parser";
    private static final Logger LOGGER = Logger.getLogger(EventParser.class);
    private static final String PARTITIONER = ",";
    private transient long duration;
    private transient int events[][];
    private transient int eventTypes;

    private transient String[] prefixes;
    private transient JButton plot;

    private transient String templates = "NB,CLL";
    private transient JTextField partitionerTf;
    private transient JTextField templatesTf;
    private transient JTabbedPane tabbedPane;
    private transient TextField plotTitle;
    private transient TextField xLabel;
    private transient TextField yLabel;
    private transient JButton remove;

    public EventParser(final JTabbedPane jTabbedPane1) {
        this.tabbedPane = jTabbedPane1;

        this.setLayout(new BorderLayout());

        final JPanel mainPanel = new JPanel(new GridLayout(0, 2, 30, 30));
        final JPanel leftPanel = new JPanel(new GridLayout(0, 1));
        final JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        this.add(new JLabel(NAME), BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);

        plot = new JButton(super.PLOT);
        plot.addActionListener(this);
        remove = new JButton(super.REMOVE);
        remove.addActionListener(this);
        rightPanel.add(new CouplePanel(plot, remove));


        partitionerTf = new JTextField(PARTITIONER);
        templatesTf = new JTextField(templates);
        leftPanel.add(new CouplePanel(new JLabel("partitioner"), partitionerTf));
        leftPanel.add(new CouplePanel(new JLabel("templates"), templatesTf));


        plotTitle = new TextField("Event Statistics");
        rightPanel.add(new CouplePanel(new JLabel("Plot title:"), plotTitle));
        xLabel = new TextField("getTime in sec");
        rightPanel.add(new CouplePanel(new JLabel("X axis Label:"), xLabel));
        yLabel = new TextField("# of Events");
        rightPanel.add(new CouplePanel(new JLabel("Y axis Label:"), yLabel));

        init();

    }


    private void init() {
        LOGGER.info("EventParser initialized");
    }

//    //TODO: add multiple Events
//    public EventParser(TraceFile f, String template) {
//
//        //LOGGER.info("EventParser initialized");
//        duration = f.getDuration();
//
//
//        final String partitioner = "-";
//        eventTypes = template.split(partitioner).length;
//
//        file = f;
//        duration = f.getDuration() / 1000 + 1;
//        events = new int[eventTypes][(int) duration];
//
//        for (int type = 0; type < eventTypes; type++) {
//            for (int j = 0; j < (int) duration; j++) {
//                events[type][j] = 0;
//            }
//        }
//
//        prefixes = new String[eventTypes];
//        final String[] templates = template.split(partitioner);
//
//
//        for (int type = 0; type < eventTypes; type++) {
//            LOGGER.info(templates[type]);
//            String delimiter = ";";
//            if (templates[type].contains(delimiter)) {
//                prefixes[type] = templates[type].substring(0, templates[type].indexOf(delimiter));
//            } else {
//                prefixes[type] = templates[type];
//            }
//            LOGGER.info(prefixes[type]);
//        }
//
//        init();
//    }

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

    //    @Override
    public ChartPanel getPlot() {
        return getPlot(true);
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
    public XYSeries[] getSeriesAggregate() {
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
                series[type].add(i, countUntil(type, i));
            }
        }
        return series;
    }

    private int countUntil(final int type, final int timeUntil) {
        int sum = 0;
        for (int i = 0; i <= timeUntil; i++) {
            sum += events[type][i];
        }
        return sum;
    }

    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plot)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + TraceFile.getInstance().getFilename() + "...");
            TraceReader reader = new TraceReader();
            reader.addObserver(this);
            reader.run();
            LOGGER.info("|--- done parsing!");
            LOGGER.info("|=== generating plot...");
            JFrame frame = new JFrame();
            frame.add(getPlot());
            frame.pack();
            frame.setVisible(true);
            LOGGER.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(remove)) {
            tabbedPane.remove(this);
        }
    }

    private void reset() {
        templates = templatesTf.getText();
        prefixes = templates.split(partitionerTf.getText());
        eventTypes = prefixes.length;

        duration = TraceFile.getInstance().getDuration() / 1000 + 1;
        LOGGER.info(eventTypes +" dur"+duration);
        events = new int[eventTypes][(int) duration];

        for (int type = 0; type < eventTypes; type++) {
            for (int j = 0; j < (int) duration; j++) {
                events[type][j] = 0;
            }
        }
    }
}
