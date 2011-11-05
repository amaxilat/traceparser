package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.TraceFile;
import eu.amaxilatis.java.traceparser.TraceMessage;
import eu.amaxilatis.java.traceparser.TraceReader;
import eu.amaxilatis.java.traceparser.panels.NodeSelectorPanel;
import eu.amaxilatis.java.traceparser.panels.couplePanel;
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
 * Time: 1:19 PM
 */
public class SendParser extends AbstractParser implements Observer, ActionListener {

    private TraceFile file;
    private static final Logger log = Logger.getLogger(SendParser.class);
    private long duration;
    private int messages[][];


    private final String delimiter = ";";
    private int type = 2;
    private String[] parts;
    public static String Name = "Send Parser";
    private JButton plotbutton;
    private JButton updatebutton;
    private String template = "CLS;%s;%t;%d";
    private JTextField templateTextField;
    private JLabel prefixTextField;
    private JCheckBox aggregateCheckbox;
    private boolean aggregate = false;
    private JTextField hiddenTextField;
    private String hidden = "";
    private String prefix;
    private JTabbedPane tabbedPane = null;
    private TextField plotTitle;
    private TextField xLabel;
    private TextField yLabel;

    public SendParser(JTabbedPane jTabbedPane1) {
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
        updatebutton = new JButton(super.REMOVE);
        updatebutton.addActionListener(this);

        rightmainpanel.add(new couplePanel(plotbutton, updatebutton));

        templateTextField = new JTextField(template);
        leftmainpanel.add(new couplePanel(new JLabel("Message Sent Template"), templateTextField));
        prefixTextField = new JLabel(prefix);

        leftmainpanel.add(new couplePanel(new JLabel("Message Sent Prefix"), prefixTextField));
        hiddenTextField = new JTextField(hidden);
        leftmainpanel.add(new couplePanel(new JLabel("Hidden Message ids"), hiddenTextField));
        aggregateCheckbox = new JCheckBox();
        aggregateCheckbox.setSelected(aggregate);
        leftmainpanel.add(new couplePanel(new JLabel("Aggregate plot"), aggregateCheckbox));

        plotTitle = new TextField("Message Statistics");
        rightmainpanel.add(new couplePanel(new JLabel("Plot title:"), plotTitle));
        xLabel = new TextField("getTime in sec");
        rightmainpanel.add(new couplePanel(new JLabel("X axis Label:"), xLabel));
        yLabel = new TextField("# of Messages");
        rightmainpanel.add(new couplePanel(new JLabel("Y axis Label:"), yLabel));


        log.info("SendParser initialized");


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
        duration = f.getDuration() / 1000 + 1;
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
        if (!NodeSelectorPanel.isSelected(m.getUrn())) return;

        try {
            if (m.getText().startsWith(prefix)) {

                final String[] mess = m.getText().split(delimiter);
                log.info("Send@" + ":" + m.getUrn() + " type:" + mess[type]);
                messages[Integer.parseInt(mess[type])][((int) ((m.getTime() - file.starttime()) / 1000))]++;
            }
        } catch (Exception e) {
            log.error(e.toString() + " Message : " + m.getText());
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

    //    @Override
    public ChartPanel getPlot() {
        return getPlot(false, aggregate, plotTitle.getText(), xLabel.getText(), yLabel.getText());
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


        XYSeriesCollection seriesCollection = new XYSeriesCollection();

        for (int types = 0; types < 255; types++) {
            XYSeries series;
            if (exists(types)) {
                if (!hidden.contains(Integer.toString(types))) {
                    series = new XYSeries("Mes. " + types);
                    for (int i = 0; i < duration; i++) {
                        if (count_until(types, i) > 0) {
                            series.add(i, messages[types][i]);
                        }
                    }
                    seriesCollection.addSeries(series);
                }
            }
        }

        XYSeries[] series = new XYSeries[seriesCollection.getSeriesCount()];
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            series[i] = seriesCollection.getSeries(i);
        }

        return series;
    }


    public XYSeries[] getSeries_aggregate() {

        XYSeriesCollection seriesCollection = new XYSeriesCollection();


        for (int types = 0; types < 255; types++) {


            if (exists(types)) {
                log.debug("class conatins : " + hidden.contains(Integer.toString(types)) + " type " + Integer.toString(types));
                if (!hidden.contains(Integer.toString(types))) {
                    XYSeries series;
                    series = new XYSeries("Mes. " + types);
                    for (int i = 0; i < duration; i++) {
                        if (count_until(types, i) > 0) {
                            series.add(i, count_until(types, i));
                        }
                    }
                    seriesCollection.addSeries(series);
                }
            }

        }
        XYSeries[] series = new XYSeries[seriesCollection.getSeriesCount()];
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            series[i] = seriesCollection.getSeries(i);
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
            sum += messages[type][i];
        }
        return sum;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(plotbutton)) {
            reset();
            log.info("|=== parsing tracefile: " + file.getFilename() + "...");
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
        hidden = hiddenTextField.getText();

        template = templateTextField.getText();
        parts = template.split(delimiter);
        init();
        prefixTextField.setText(prefix);

    }
}
