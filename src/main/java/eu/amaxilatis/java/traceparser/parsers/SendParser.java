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
 *
 */
public class SendParser extends GenericParser implements Observer, ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendParser.class);
    public static final String NAME = "Send Parser";

    private transient final JTabbedPane tabbedPane;
    private transient final JButton plotButton;
    private transient final JButton updateButton;
    private transient final JLabel prefixLabel;
    private transient final JCheckBox aggregateCheckbox;
    private transient final JTextField templateTf;
    private transient final JTextField hiddenTf;
    private transient final JTextField plotTitleTf;
    private transient final JTextField xLabelTf;
    private transient final JTextField yLabelTf;

    private transient long duration;
    private transient int messages[][];
    private static String delimiter = ";";
    private transient int type = 2;
    private transient String[] parts;
    private transient String template = "CLS;%s;%t;%d";
    private transient boolean aggregate = false;
    private transient String hidden = "";
    private transient String prefix;

    /**
     * @param tabbedPane
     */
    public SendParser(final JTabbedPane tabbedPane) {
        super(NAME);
        this.tabbedPane = tabbedPane;

        plotButton = new JButton(super.PLOT);
        plotButton.addActionListener(this);
        updateButton = new JButton(super.REMOVE);
        updateButton.addActionListener(this);

        addRight(plotButton, updateButton);

        templateTf = new JTextField(template);
        addLeft(new JLabel("Message Sent Template"), templateTf);
        prefixLabel = new JLabel(prefix);

        addLeft(new JLabel("Message Sent Prefix"), prefixLabel);
        hiddenTf = new JTextField(hidden);
        addLeft(new JLabel("Hidden Message ids"), hiddenTf);
        aggregateCheckbox = new JCheckBox();
        aggregateCheckbox.setSelected(aggregate);
        addLeft(new JLabel("Aggregate plot"), aggregateCheckbox);

        plotTitleTf = new JTextField("Message Statistics");
        addRight(new JLabel("Plot title:"), plotTitleTf);
        xLabelTf = new JTextField("getTime in sec");
        addRight(new JLabel("X axis Label:"), xLabelTf);
        yLabelTf = new JTextField("# of Messages");
        addRight(new JLabel("Y axis Label:"), yLabelTf);

        LOGGER.info("SendParser initialized");
    }

    /**
     *
     */
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

    /**
     * @param observable
     * @param object
     */
    public void update(final Observable observable, final Object object) {
        final TraceMessage message = (TraceMessage) object;
        if (NodeSelectorPanel.isSelected(message.getUrn())) {
            try {
                if (message.getText().startsWith(prefix)) {

                    final String[] mess = message.getText().split(delimiter);
                    LOGGER.debug("Send@" + ":" + message.getUrn() + " type:" + mess[type]);
                    messages[Integer.parseInt(mess[type])][((int) ((message.getTime() - TraceFile.getInstance().getStartTime()) / 1000))]++;
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " Message : " + message.getText());
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

    /**
     * @param type
     * @return
     */
    boolean exists(final int type) {
        for (int i = 0; i < duration; i++) {
            if (messages[type][i] != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
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

    /**
     * @return
     */
    public XYSeries[] getAggegatedSeries() {
        final XYSeriesCollection seriesCollection = new XYSeriesCollection();

        for (int types = 0; types < 255; types++) {
            if ((exists(types)) && (!hidden.contains(Integer.toString(types)))) {
//                LOGGER.debug("class conatins : " + hidden.contains(Integer.toString(types)) + " type " + Integer.toString(types));
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

    /**
     * @param type
     * @param time_until
     * @return
     */
    private int countUntil(final int type, final int time_until) {
        int sum = 0;
        for (int i = 0; i <= time_until; i++) {
            sum += messages[type][i];
        }
        return sum;
    }

    /**
     * @param actionEvent
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotButton)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + TraceFile.getInstance().getFilename() + "...");
            final TraceReader traceReader = new TraceReader();
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

    /**
     *
     */
    private void reset() {
        duration = TraceFile.getInstance().getDuration() / 1000 + 1;
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
