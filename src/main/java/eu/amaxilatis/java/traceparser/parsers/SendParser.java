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


public class SendParser extends AbstractParser implements Observer, ActionListener {

    private static final Logger LOGGER = Logger.getLogger(SendParser.class);
    public static String NAME = "Send Parser";

    private final JTabbedPane tabbedPane;
    private final JButton plotButton;
    private final JButton updateButton;
    private final JLabel prefixLabel;
    private final JCheckBox aggregateCheckbox;
    private final JTextField templateTf;
    private final JTextField hiddenTf;
    private final JTextField plotTitleTf;
    private final JTextField xLabelTf;
    private final JTextField yLabelTf;

    private TraceFile file;
    private long duration;
    private int messages[][];
    private static String delimiter = ";";
    private int type = 2;
    private String[] parts;
    private String template = "CLS;%s;%t;%d";
    private boolean aggregate = false;
    private String hidden = "";
    private String prefix;


    public SendParser(final JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        this.setLayout(new BorderLayout());

        final JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        final JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        final JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel(NAME), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        plotButton = new JButton(super.PLOT);
        plotButton.addActionListener(this);
        updateButton = new JButton(super.REMOVE);
        updateButton.addActionListener(this);

        rightmainpanel.add(new CouplePanel(plotButton, updateButton));

        templateTf = new JTextField(template);
        leftmainpanel.add(new CouplePanel(new JLabel("Message Sent Template"), templateTf));
        prefixLabel = new JLabel(prefix);

        leftmainpanel.add(new CouplePanel(new JLabel("Message Sent Prefix"), prefixLabel));
        hiddenTf = new JTextField(hidden);
        leftmainpanel.add(new CouplePanel(new JLabel("Hidden Message ids"), hiddenTf));
        aggregateCheckbox = new JCheckBox();
        aggregateCheckbox.setSelected(aggregate);
        leftmainpanel.add(new CouplePanel(new JLabel("Aggregate plot"), aggregateCheckbox));

        plotTitleTf = new JTextField("Message Statistics");
        rightmainpanel.add(new CouplePanel(new JLabel("Plot title:"), plotTitleTf));
        xLabelTf = new JTextField("getTime in sec");
        rightmainpanel.add(new CouplePanel(new JLabel("X axis Label:"), xLabelTf));
        yLabelTf = new JTextField("# of Messages");
        rightmainpanel.add(new CouplePanel(new JLabel("Y axis Label:"), yLabelTf));

        LOGGER.info("SendParser initialized");
    }


    private void init() {
        prefix = parts[0];
        if (parts[1].equals("%t")) {
            type = 1;
        }
        if (parts[2].equals("%t")) {
            type = 2;
        }
        if (parts[3].equals("%t")) {
            type = 3;
        }
        LOGGER.info("SendParser initialized");
    }

    public void update(final Observable observable, final Object object) {
        final TraceMessage message = (TraceMessage) object;
        if (NodeSelectorPanel.isSelected(message.getUrn())) {
            try {
                if (message.getText().startsWith(prefix)) {

                    final String[] mess = message.getText().split(delimiter);
                    LOGGER.info("Send@" + ":" + message.getUrn() + " type:" + mess[type]);
                    messages[Integer.parseInt(mess[type])][((int) ((message.getTime() - file.getStartTime()) / 1000))]++;
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " Message : " + message.getText());
            }
        }
    }


    public ChartPanel getPlot(final boolean aggregate) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] messageTypes;
        if (aggregate) {
            messageTypes = getAggegatedSeries();
        } else {
            messageTypes = getSeries();
        }

        for (XYSeries messageType : messageTypes) {
            dataset.addSeries(messageType);
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                plotTitleTf.getText(),
                xLabelTf.getText(),
                yLabelTf.getText(),
                dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.white);

        return new ChartPanel(chart);
    }


    boolean exists(final int type) {
        for (int i = 0; i < duration; i++) {
            if (messages[type][i] != 0) {
                return true;
            }
        }
        return false;
    }


    public XYSeries[] getSeries() {
        final XYSeriesCollection seriesCollection = new XYSeriesCollection();

        for (int types = 0; types < 255; types++) {
            if (exists(types) && (!hidden.contains(Integer.toString(types)))) {
                final XYSeries series = new XYSeries("Mes. " + types);
                for (int i = 0; i < duration; i++) {
                    if (countUntil(types, i) > 0) {
                        series.add(i, messages[types][i]);
                    }
                }
                seriesCollection.addSeries(series);
            }
        }

        XYSeries[] series = new XYSeries[seriesCollection.getSeriesCount()];
        for (
                int i = 0;
                i < seriesCollection.getSeriesCount(); i++)

        {
            series[i] = seriesCollection.getSeries(i);
        }

        return series;
    }


    public XYSeries[] getAggegatedSeries() {
        final XYSeriesCollection seriesCollection = new XYSeriesCollection();

        for (int types = 0; types < 255; types++) {
            if ((exists(types)) && (!hidden.contains(Integer.toString(types)))) {
                LOGGER.debug("class conatins : " + hidden.contains(Integer.toString(types)) + " type " + Integer.toString(types));

                final XYSeries series = new XYSeries("Mes. " + types);
                for (int i = 0; i < duration; i++) {
                    if (countUntil(types, i) > 0) {
                        series.add(i, countUntil(types, i));
                    }
                }
                seriesCollection.addSeries(series);

            }
        }
        XYSeries[] series = new XYSeries[seriesCollection.getSeriesCount()];
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            series[i] = seriesCollection.getSeries(i);
        }
        return series;
    }

    //    @Override
    public void setTraceFile(final TraceFile file) {
        this.file = file;
        reset();
    }

    private int countUntil(final int type, final int time_until) {
        int sum = 0;
        for (int i = 0; i <= time_until; i++) {
            sum += messages[type][i];
        }
        return sum;
    }

    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotButton)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + file.getFilename() + "...");
            final TraceReader traceReader = new TraceReader(file);
            traceReader.addObserver(this);
            traceReader.run();
            LOGGER.info("|--- done parsing!");
            LOGGER.info("|=== generating plot...");
            final JFrame frame = new JFrame();
            frame.add(getPlot(aggregate));
            frame.pack();
            frame.setVisible(true);
            LOGGER.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(updateButton)) {
            tabbedPane.remove(this);

        }
    }

    private void reset() {
        duration = file.getDuration() / 1000 + 1;
        messages = new int[255][(int) duration];
        for (int i = 0; i < 255; i++) {
            for (int j = 0; j < (int) duration; j++) {
                messages[i][j] = 0;
            }
        }
        aggregate = aggregateCheckbox.isSelected();
        hidden = hiddenTf.getText();

        template = templateTf.getText();
        parts = template.split(delimiter);
        init();
        prefixLabel.setText(prefix);

    }
}
