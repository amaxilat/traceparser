/*
 * ParseOrPlotFrame.java
 *
 * Created on Jun 22, 2011, 5:23:34 PM
 */

package eu.amaxilatis.java.traceparser;


import eu.amaxilatis.java.traceparser.panels.*;
import eu.amaxilatis.java.traceparser.parsers.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author amaxilatis
 */
public class TraceParserFrame extends javax.swing.JFrame implements ActionListener {

    // Variables declaration - do not modify//GEN-BEGIN:variables

    private static final Logger LOGGER = Logger.getLogger(TraceParserFrame.class);
    private static final String PROP_FILE = "traceparser.properties";


    private transient javax.swing.JButton addParser;
    private transient JList availableParsers;
    private transient JButton openFileChooser;
    private transient JButton refreshTrace;


    private transient javax.swing.JLabel selectedFileText;
    private transient javax.swing.JLabel linesFileText;
    private transient javax.swing.JLabel durationFileText;
    private transient javax.swing.JLabel nodesFileText;

    private transient JTabbedPane jTabbedPane1;


    /**
     * Creates new form TraceParserFrame
     */
    public TraceParserFrame() {

        initComponents();
        this.setVisible(true);

    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        final Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROP_FILE));
        } catch (IOException e) {
            LOGGER.error("could not load property file!");
        }

        jTabbedPane1 = new JTabbedPane();


        final JPanel fileOptionsPanel = new JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileOptionsPanel.setLayout(new java.awt.GridLayout(0, 3, 10, 10));

        addParser = new javax.swing.JButton("Add Parser");
        addParser.addActionListener(this);

        JButton saveProperties = new JButton("Save Properties");
        saveProperties.addActionListener(this);

        final DefaultListModel listModel = new DefaultListModel();
        listModel.addElement(NeighborhoodParser.NAME);
        listModel.addElement(ClustersParser.NAME);
        listModel.addElement(EventParser.NAME);
        listModel.addElement(SendParser.NAME);
        listModel.addElement(SensorAggregationParser.NAME);
        listModel.addElement(ClusterOverlapParser.NAME);
        availableParsers = new JList(listModel);


        fileOptionsPanel.add(new JLabel(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("favicon.png"), "")));

        selectedFileText = new JLabel("no file selected");
        fileOptionsPanel.add(selectedFileText);
        setSize(fileOptionsPanel);

        openFileChooser = new JButton("Open File...");
        openFileChooser.addActionListener(this);
        setSize(openFileChooser);
        fileOptionsPanel.add(openFileChooser);


        linesFileText = new JLabel("0");
        fileOptionsPanel.add(new JLabel("Trace Lines"));
        fileOptionsPanel.add(linesFileText);

        refreshTrace = new JButton("Refresh Trace");
        refreshTrace.addActionListener(this);
        fileOptionsPanel.add(refreshTrace);

        durationFileText = new JLabel("0");
        fileOptionsPanel.add(new JLabel("Trace Duration"));
        fileOptionsPanel.add(durationFileText);
        fileOptionsPanel.add(new JLabel("")); //for design purposes

        nodesFileText = new JLabel("0");
        fileOptionsPanel.add(new JLabel("Total Nodes in Trace"));
        fileOptionsPanel.add(nodesFileText);


        jTabbedPane1.addTab("File Options", fileOptionsPanel);
        jTabbedPane1.addTab("Plot Options", new PlotterControlPanel());
        jTabbedPane1.addTab("Node Selection", new NodeSelectorPanel());


        getContentPane().setLayout(new BorderLayout());
        final JPanel buttonsmain = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonsmain.add(availableParsers);
        buttonsmain.add(addParser);
        buttonsmain.add(saveProperties);
        getContentPane().add(buttonsmain, BorderLayout.PAGE_END);
        jTabbedPane1.setPreferredSize(new Dimension(1000, 500));
        getContentPane().add(jTabbedPane1, BorderLayout.CENTER);


        openTrace(properties.getProperty("parser.filename"));
        pack();

    }

//    /**
//     * @param args the command line arguments
//     */
//
//    public static void main(String args[]) {
//
//
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TraceParserFrame().setVisible(true);
//
//            }
//        });
//    }

    private void setSize(final Component obj) {
        obj.setPreferredSize(new Dimension(150, 40));
        obj.setMaximumSize(new Dimension(150, 40));
        obj.setMinimumSize(new Dimension(150, 40));

    }


    // End of variables declaration//GEN-END:variables

    public void actionPerformed(final ActionEvent actionEvent) {
        final Object source = actionEvent.getSource();
        if (source.equals(addParser)) {
            addParser(availableParsers.getSelectedValue().toString());
        } else if ((source.equals(openFileChooser)) || ((source.equals(selectedFileText)))) {
            final JFileChooser chooser = new JFileChooser("~/");
            chooser.setDialogTitle("Select trace file to load");
            final int returnVal = chooser.showOpenDialog(chooser);
            if ((returnVal == JFileChooser.APPROVE_OPTION) && (chooser.getSelectedFile().canRead())) {
                final String filename2open = chooser.getSelectedFile().getAbsolutePath();
                openTrace(filename2open);
            }

        } else if (source.equals(refreshTrace)) {
            openTrace(TraceFile.getInstance().getFilename());
        }

    }

    private void addParser(final String title) {
        try {
            GenericParser panel2add = null;

            if (title.equals(NeighborhoodParser.NAME)) {
                panel2add = new NeighborhoodParser(jTabbedPane1);
            } else if (title.equals(ClustersParser.NAME)) {
                panel2add = new ClustersParser(jTabbedPane1);
            } else if (title.equals(EventParser.NAME)) {
                panel2add = new EventParser(jTabbedPane1);
            } else if (title.equals(SensorAggregationParser.NAME)) {
                panel2add = new SensorAggregationParser(jTabbedPane1);
            } else if (title.equals(SendParser.NAME)) {
                panel2add = new SendParser(jTabbedPane1);
            } else if (title.equals(ClusterOverlapParser.NAME)) {
                panel2add = new ClusterOverlapParser(jTabbedPane1);
            }
            if (panel2add != null) {
                jTabbedPane1.addTab(title, panel2add);
                jTabbedPane1.updateUI();
            }
        } catch (Exception e1) {
            LOGGER.debug(e1);
        }
    }

    private void openTrace(final String filename) {
        LOGGER.debug("opening " + filename);
        durationFileText.setText("calculating");
        selectedFileText.setText(filename);
        try {
            TraceFile.getInstance().setFile(filename, new FileInputStream(filename));
        } catch (IOException exception) {
            LOGGER.error(exception);
        }
        durationFileText.setText(TraceFile.getInstance().getDuration() / 60000 + " min= " + TraceFile.getInstance().getDuration() / 1000 + " sec");
        nodesFileText.setText(TraceFile.getInstance().getNodeSize() + " nodes");
        linesFileText.setText(TraceFile.getInstance().getLines() + " getLines");
        NodeSelectorPanel.update();
    }
}

