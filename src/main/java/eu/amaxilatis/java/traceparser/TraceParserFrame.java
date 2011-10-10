/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ParseOrPlotFrame.java
 *
 * Created on Jun 22, 2011, 5:23:34 PM
 */

package eu.amaxilatis.java.traceparser;

import eu.amaxilatis.java.traceparser.parsers.ClustersParser;
import eu.amaxilatis.java.traceparser.parsers.EventParser;
import eu.amaxilatis.java.traceparser.parsers.NeighborhoodParser;
import eu.amaxilatis.java.traceparser.parsers.SendParser;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author amaxilatis
 */
public class TraceParserFrame extends javax.swing.JFrame implements ActionListener {

    // Variables declaration - do not modify//GEN-BEGIN:variables

    private static final Logger log = Logger.getLogger(TraceParserFrame.class);
    private static final String propfilename = "traceparser.properties";


    private javax.swing.JButton generatePlotButton;
    private javax.swing.JButton generateFileButton;
    private javax.swing.JButton savePropertiesButton;
    private JButton openFileChooserButton;
    private JButton refreshTraceButton;

    private final javax.swing.JLabel[] parserOptionsLabel = new javax.swing.JLabel[3];
    private final javax.swing.JTextField[] parserOptionsText = new javax.swing.JTextField[3];
    private javax.swing.JLabel selectedFileText;
    private javax.swing.JLabel linesFileText;
    private javax.swing.JLabel durationFileText;
    private javax.swing.JLabel nodesFileText;

    private JRadioButton aggregatePlots;
    private JRadioButton messagesPlots;
    private JRadioButton clustersPlots;
    private JRadioButton eventsPlots;
    private JRadioButton neighborhoodPlots;


    private final JTextField[] labelsMessages = new JTextField[3];
    private final JTextField[] labelsClusters = new JTextField[3];
    private final JTextField[] labelsEvents = new JTextField[3];
    private final JTextField[] labelsNeighborhood = new JTextField[3];


    private TraceFile mytracefile;


    private Properties properties;


    /**
     * Creates new form TraceParserFrame
     */
    public TraceParserFrame() {
        //log = new logger();
        //log.setLevel(logger.EXTRA);
        initComponents();
        this.setVisible(true);
//        this.show();
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(propfilename));
        } catch (IOException e) {
            log.error("could not load property file!");
        }

        JTabbedPane jTabbedPane1 = new JTabbedPane();

        JPanel fileOptionsPanel = new JPanel();
        JPanel parserOptionsPanel = new JPanel();
        JPanel plotterOptionsPanel = new JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileOptionsPanel.setLayout(new java.awt.GridLayout(0, 3, 10, 10));
        parserOptionsPanel.setLayout(new java.awt.GridLayout(3, 2, 10, 10));
        plotterOptionsPanel.setLayout(new java.awt.GridLayout(0, 4, 10, 10));

        generatePlotButton = new javax.swing.JButton("Generate Plot");
        generatePlotButton.addActionListener(this);

        generateFileButton = new javax.swing.JButton("Generate File");
        generateFileButton.addActionListener(this);

        savePropertiesButton = new JButton("Save Properties");
        savePropertiesButton.addActionListener(this);

        fileOptionsPanel.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("favicon.png"), "")));

        selectedFileText = new JLabel("no file selected");
        fileOptionsPanel.add(selectedFileText);
        setSize(fileOptionsPanel, 150, 40);

        openFileChooserButton = new JButton("Open File...");
        openFileChooserButton.addActionListener(this);
        setSize(openFileChooserButton, 150, 40);
        fileOptionsPanel.add(openFileChooserButton);


        linesFileText = new JLabel("0");
        fileOptionsPanel.add(new JLabel("Trace Lines"));
        fileOptionsPanel.add(linesFileText);

        refreshTraceButton = new JButton("Refresh Trace");
        refreshTraceButton.addActionListener(this);
        fileOptionsPanel.add(refreshTraceButton);

        durationFileText = new JLabel("0");
        fileOptionsPanel.add(new JLabel("Trace Duration"));
        fileOptionsPanel.add(durationFileText);
        fileOptionsPanel.add(new JLabel("")); //for design purposes

        nodesFileText = new JLabel("0");
        fileOptionsPanel.add(new JLabel("Total Nodes in Trace"));
        fileOptionsPanel.add(nodesFileText);


        final String[] parserOptionsTexts = properties.getProperty("parser.templates").split(",");
        final String[] parserOptionsLabels = {"Send Text", "Cluster Text", "Event Text"};
        for (int i = 0; i < 3; i++) {
            parserOptionsLabel[i] = new javax.swing.JLabel(parserOptionsLabels[i]);
            parserOptionsText[i] = new javax.swing.JTextField(parserOptionsTexts[i]);
            parserOptionsPanel.add(parserOptionsLabel[i]);
            parserOptionsPanel.add(parserOptionsText[i]);
        }


        messagesPlots = new JRadioButton("enable");
        clustersPlots = new JRadioButton("enable");
        eventsPlots = new JRadioButton("enable");
        neighborhoodPlots = new JRadioButton("enable");
        messagesPlots.setSelected(properties.getProperty("plotter.messages").equals("true"));
        clustersPlots.setSelected(properties.getProperty("plotter.clusters").equals("true"));
        eventsPlots.setSelected(properties.getProperty("plotter.events").equals("true"));
        neighborhoodPlots.setSelected(properties.getProperty("plotter.neighborhood").equals("true"));

        for (int i = 0; i < 3; i++) {

            labelsMessages[i] = new JTextField(properties.getProperty("plotter.labels.messages").split(",")[i]);
            labelsClusters[i] = new JTextField(properties.getProperty("plotter.labels.clusters").split(",")[i]);
            labelsEvents[i] = new JTextField(properties.getProperty("plotter.labels.events").split(",")[i]);
            labelsNeighborhood[i] = new JTextField(properties.getProperty("plotter.labels.neighborhood").split(",")[i]);
        }

        plotterOptionsPanel.add(new JLabel("Plot Messages"));
        plotterOptionsPanel.add(new JLabel("Title"));
        plotterOptionsPanel.add(new JLabel("xLabel"));
        plotterOptionsPanel.add(new JLabel("yLabel"));
        plotterOptionsPanel.add(messagesPlots);
        plotterOptionsPanel.add(labelsMessages[0]);
        plotterOptionsPanel.add(labelsMessages[1]);
        plotterOptionsPanel.add(labelsMessages[2]);

        plotterOptionsPanel.add(new JLabel("Plot Clusters"));
        plotterOptionsPanel.add(new JLabel("Title"));
        plotterOptionsPanel.add(new JLabel("xLabel"));
        plotterOptionsPanel.add(new JLabel("yLabel"));
        plotterOptionsPanel.add(clustersPlots);
        plotterOptionsPanel.add(labelsClusters[0]);
        plotterOptionsPanel.add(labelsClusters[1]);
        plotterOptionsPanel.add(labelsClusters[2]);

        plotterOptionsPanel.add(new JLabel("Plot Events"));
        plotterOptionsPanel.add(new JLabel("Title"));
        plotterOptionsPanel.add(new JLabel("xLabel"));
        plotterOptionsPanel.add(new JLabel("yLabel"));
        plotterOptionsPanel.add(eventsPlots);
        plotterOptionsPanel.add(labelsEvents[0]);
        plotterOptionsPanel.add(labelsEvents[1]);
        plotterOptionsPanel.add(labelsEvents[2]);

        plotterOptionsPanel.add(new JLabel("Plot Neigborhood"));
        plotterOptionsPanel.add(new JLabel("Title"));
        plotterOptionsPanel.add(new JLabel("xLabel"));
        plotterOptionsPanel.add(new JLabel("yLabel"));
        plotterOptionsPanel.add(neighborhoodPlots);
        plotterOptionsPanel.add(labelsNeighborhood[0]);
        plotterOptionsPanel.add(labelsNeighborhood[1]);
        plotterOptionsPanel.add(labelsNeighborhood[2]);


        aggregatePlots = new JRadioButton("Aggregate");
        aggregatePlots.setSelected(properties.getProperty("plotter.aggregate").equals("true"));
        plotterOptionsPanel.add(aggregatePlots);


        jTabbedPane1.addTab("File Options", fileOptionsPanel);
        jTabbedPane1.addTab("Parser Options", parserOptionsPanel);
        jTabbedPane1.addTab("Plotter Options", plotterOptionsPanel);


        getContentPane().setLayout(new BorderLayout());
        final JPanel buttonsmain = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonsmain.add(generateFileButton);
        buttonsmain.add(generatePlotButton);
        buttonsmain.add(savePropertiesButton);
        getContentPane().add(buttonsmain, BorderLayout.PAGE_END);
        jTabbedPane1.setPreferredSize(new Dimension(1000, 500));
        getContentPane().add(jTabbedPane1, BorderLayout.CENTER);


        open_trace(properties.getProperty("parser.filename"));
        pack();

    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    public static void main(String args[]) {


        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TraceParserFrame().setVisible(true);

            }
        });
    }

    private void setSize(Component obj, int x, int y) {
        obj.setPreferredSize(new Dimension(x, y));
        obj.setMaximumSize(new Dimension(x, y));
        obj.setMinimumSize(new Dimension(x, y));

    }


    // End of variables declaration//GEN-END:variables

    public void actionPerformed(ActionEvent actionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
        final Object e = actionEvent.getSource();
        EventParser eventparser;
        ClustersParser clustersparser;
        SendParser sendparser;
        if (e.equals(generatePlotButton)) {


            TraceReader tracereader = new TraceReader(mytracefile);

            sendparser = new SendParser(mytracefile, parserOptionsText[0].getText());
            clustersparser = new ClustersParser(mytracefile, parserOptionsText[1].getText());
            eventparser = new EventParser(mytracefile, parserOptionsText[2].getText());
            NeighborhoodParser neighborhoodparser = new NeighborhoodParser(mytracefile, "NB;");
//            SemanticsParser sp = new SemanticsParser(mytracefile, "");
            if (messagesPlots.isSelected())
                tracereader.addObserver(sendparser);
            if (clustersPlots.isSelected())
                tracereader.addObserver(clustersparser);
            if (eventsPlots.isSelected())
                tracereader.addObserver(eventparser);
            tracereader.addObserver(neighborhoodparser);
//            tracereader.addObserver(sp);
            tracereader.run();

            final boolean aggPlot = aggregatePlots.isSelected();
            if (messagesPlots.isSelected())
                presentPlot(sendparser.getPlot(labelsMessages[0].getText().equals(""), aggPlot, labelsMessages[0].getText(), labelsMessages[1].getText(), labelsMessages[2].getText()));
            if (clustersPlots.isSelected())
                presentPlot(clustersparser.getPlot(labelsClusters[0].getText().equals(""), aggPlot, labelsClusters[0].getText(), labelsClusters[1].getText(), labelsClusters[2].getText()));
            if (eventsPlots.isSelected())
                presentPlot(eventparser.getPlot(labelsEvents[0].getText().equals(""), aggPlot, labelsEvents[0].getText(), labelsEvents[1].getText(), labelsEvents[2].getText()));
            if (neighborhoodPlots.isSelected())
                presentPlot(neighborhoodparser.getPlot(labelsNeighborhood[0].getText().equals(""), aggPlot, labelsNeighborhood[0].getText(), labelsNeighborhood[1].getText(), labelsNeighborhood[2].getText()));

//            presentPlot(sp.getPlot(false, false, "Semantics", "time is seconds", "# of members"));

        } else if (e.equals(generateFileButton)) {

            TraceReader tracereader = new TraceReader(mytracefile);

            sendparser = new SendParser(mytracefile, parserOptionsText[0].getText());
            clustersparser = new ClustersParser(mytracefile, parserOptionsText[1].getText());
            eventparser = new EventParser(mytracefile, parserOptionsText[2].getText());

            tracereader.addObserver(sendparser);
            tracereader.addObserver(clustersparser);
            tracereader.addObserver(eventparser);

            tracereader.run();

        } else if ((e.equals(openFileChooserButton)) || ((e.equals(selectedFileText)))) {
            JFileChooser chooser = new JFileChooser("~/");
            chooser.setDialogTitle("Select trace file to load");
            int returnVal = chooser.showOpenDialog(chooser);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (chooser.getSelectedFile().canRead()) {
                    final String filename2open = chooser.getSelectedFile().getAbsolutePath();
                    open_trace(filename2open);
                }
            }
        } else if (e.equals(refreshTraceButton)) {
            open_trace(mytracefile.filename());

        } else if (e.equals(savePropertiesButton))

        {
            log.info("Writing properties");
            properties.setProperty("parser.filename", mytracefile.filename());
            String parserTemplates = "";
            for (int i = 0; i < properties.getProperty("parser.templates").split(",").length; i++) {
                parserTemplates += parserOptionsText[i].getText() + ",";
            }

            properties.setProperty("parser.templates", parserTemplates);
            log.info("parset.templates=" + parserTemplates.substring(0, parserTemplates.length() - 1));

            log.info("plotter.aggregate=" + aggregatePlots.isSelected());
            properties.setProperty("plotter.aggregate", aggregatePlots.isSelected() ? "true" : "false");
            log.info("plotter.messages=" + aggregatePlots.isSelected());
            properties.setProperty("plotter.messages", messagesPlots.isSelected() ? "true" : "false");
            log.info("plotter.clusters=" + aggregatePlots.isSelected());
            properties.setProperty("plotter.clusters", clustersPlots.isSelected() ? "true" : "false");
            log.info("plotter.events=" + aggregatePlots.isSelected());
            properties.setProperty("plotter.events", eventsPlots.isSelected() ? "true" : "false");
            log.info("plotter.neighborhood=" + neighborhoodPlots.isSelected());
            properties.setProperty("plotter.neighborhood", neighborhoodPlots.isSelected() ? "true" : "false");


            properties.setProperty("plotter.labels.messages", labelsMessages[0].getText() + "," + labelsMessages[1].getText() + "," + labelsMessages[2].getText());
            properties.setProperty("plotter.labels.clusters", labelsClusters[0].getText() + "," + labelsClusters[1].getText() + "," + labelsClusters[2].getText());
            properties.setProperty("plotter.labels.events", labelsEvents[0].getText() + "," + labelsEvents[1].getText() + "," + labelsEvents[2].getText());
            properties.setProperty("plotter.labels.neighborhood", labelsNeighborhood[0].getText() + "," + labelsNeighborhood[1].getText() + "," + labelsNeighborhood[2].getText());

            // Write properties file.
            try {
                properties.store(new FileOutputStream(propfilename), null);
            } catch (IOException a) {
                log.warn("Could not write properties file");

            }
        }

    }

    private void open_trace(String filename) {
        log.debug("opening " + filename);
        durationFileText.setText("calculating");
        selectedFileText.setText(filename);
        mytracefile = new TraceFile(filename);
        durationFileText.setText(mytracefile.duration() / 60000 + " min");
        nodesFileText.setText(mytracefile.nodesize() + " nodes");
        linesFileText.setText(mytracefile.lines() + " lines");
    }


    private void presentPlot(ChartPanel plot) {
        JFrame jf = new JFrame("Plot");
        jf.setVisible(true);
        jf.add(plot);
        jf.pack();

    }

}

