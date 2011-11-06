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

    public static final String Name = "Events Parser";

    private TraceFile file;
    private static final Logger LOGGER = Logger.getLogger(EventParser.class);
    private static final String PARTITIONER = ",";
    private long duration;
    private int events[][];
    private int eventTypes;

    private String[] prefixes;
    private JButton plotbutton;

    String templates = "NB,CLL";
    private JTextField partitionerTextField;
    private JTextField templatesTextField;
    private JTabbedPane tabbedPane;
    private TextField plotTitle;
    private TextField xLabel;
    private TextField yLabel;
    private JButton removeButton;

    public EventParser(JTabbedPane jTabbedPane1) {
        this.tabbedPane = jTabbedPane1;

        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel(Name), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        plotbutton = new JButton(super.PLOT);
        plotbutton.addActionListener(this);
        removeButton = new JButton(super.REMOVE);
        removeButton.addActionListener(this);
        rightmainpanel.add(new CouplePanel(plotbutton, removeButton));


        partitionerTextField = new JTextField(PARTITIONER);
        templatesTextField = new JTextField(templates);
        leftmainpanel.add(new CouplePanel(new JLabel("partitioner"), partitionerTextField));
        leftmainpanel.add(new CouplePanel(new JLabel("templates"), templatesTextField));


        plotTitle = new TextField("Event Statistics");
        rightmainpanel.add(new CouplePanel(new JLabel("Plot title:"), plotTitle));
        xLabel = new TextField("getTime in sec");
        rightmainpanel.add(new CouplePanel(new JLabel("X axis Label:"), xLabel));
        yLabel = new TextField("# of Events");
        rightmainpanel.add(new CouplePanel(new JLabel("Y axis Label:"), yLabel));

        init();

    }


    private void init() {
        LOGGER.info("EventParser initialized");
    }

    //TODO: add multiple Events
    public EventParser(TraceFile f, String template) {

        //LOGGER.info("EventParser initialized");
        duration = f.getDuration();


        final String partitioner = "-";
        eventTypes = template.split(partitioner).length;

        file = f;
        duration = f.getDuration() / 1000 + 1;
        events = new int[eventTypes][(int) duration];

        for (int type = 0; type < eventTypes; type++) {
            for (int j = 0; j < (int) duration; j++) {
                events[type][j] = 0;
            }
        }

        prefixes = new String[eventTypes];
        final String[] templates = template.split(partitioner);


        for (int type = 0; type < eventTypes; type++) {
            LOGGER.info(templates[type]);
            String delimiter = ";";
            if (templates[type].contains(delimiter)) {
                prefixes[type] = templates[type].substring(0, templates[type].indexOf(delimiter));
            } else {
                prefixes[type] = templates[type];
            }
            LOGGER.info(prefixes[type]);
        }

        init();
    }

    public void update(Observable observable, Object o) {
        final TraceMessage m = (TraceMessage) o;
        if (!NodeSelectorPanel.isSelected(m.getUrn())) return;

        for (int type = 0; type < eventTypes; type++) {
            if (m.getText().contains(prefixes[type])) {
                //LOGGER.info("Event@" + m.getTime() + ":" + m.getUrn());
                events[type][((int) ((m.getTime() - file.getStartTime()) / 1000))]++;
            }
        }
    }

    public ChartPanel getPlot(boolean has_title, boolean aggregate) {

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
                plotTitle.getText(),
                xLabel.getText(),
                yLabel.getText(),
                dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.white);
        return new ChartPanel(chart);
    }

    //    @Override
    public ChartPanel getPlot() {
        return getPlot(false, true);
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

    //    @Override
    public void setTraceFile(TraceFile file) {
        this.file = file;
        reset();
    }

    public void setTemplate(String template) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

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

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotbutton)) {
            reset();
            LOGGER.info("|=== parsing tracefile: " + file.getFilename() + "...");
            TraceReader a = new TraceReader(file);
            a.addObserver(this);
            a.run();
            LOGGER.info("|--- done parsing!");
            LOGGER.info("|=== generating plot...");
            JFrame jnew = new JFrame();
            jnew.add(getPlot());
            jnew.pack();
            jnew.setVisible(true);
            LOGGER.info("|--- presenting plot...");
        } else if (actionEvent.getSource().equals(removeButton)) {
            tabbedPane.remove(this);
        }
    }

    private void reset() {
        templates = templatesTextField.getText();
        prefixes = templates.split(partitionerTextField.getText());
        eventTypes = prefixes.length;

        duration = file.getDuration() / 1000 + 1;
        events = new int[eventTypes][(int) duration];

        for (int type = 0; type < eventTypes; type++) {
            for (int j = 0; j < (int) duration; j++) {
                events[type][j] = 0;
            }
        }
    }
}
