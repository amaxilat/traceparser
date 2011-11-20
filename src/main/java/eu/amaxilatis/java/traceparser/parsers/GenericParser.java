package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.panels.CouplePanel;

import javax.swing.*;
import java.awt.*;

public class GenericParser extends JPanel {

    private final JPanel mainPanel;
    private final JPanel leftPanel;
    private final JPanel rightPanel;

    /**
     * default constructor
     */
    protected GenericParser(final String name) {
        this.setLayout(new BorderLayout());
        mainPanel = new JPanel(new GridLayout(0, 2, 30, 30));
        leftPanel = new JPanel(new GridLayout(0, 1));
        rightPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        this.add(new JLabel(name), BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
    }

    public static final String REMOVE = "Remove";
    public static final String PLOT = "Plot";

    void addLeft(Component first, Component second) {
        leftPanel.add(new CouplePanel(first, second));

    }

    void addRight(Component first, Component second) {
        rightPanel.add(new CouplePanel(first, second));
    }

};


