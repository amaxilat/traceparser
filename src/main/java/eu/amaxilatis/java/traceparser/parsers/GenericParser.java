package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.panels.CouplePanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Component;

/**
 * Superclass for all parsers.
 */
public class GenericParser extends JPanel {
    /**
     * remove button text.
     */
    public static final String REMOVE = "Remove";
    /**
     * plot button text.
     */
    public static final String PLOT = "Plot";

    /**
     * left side of the panel.
     */
    private final transient JPanel leftPanel;
    /**
     * right side of the panel.
     */
    private final transient JPanel rightPanel;
    /**
     * spacing between objects.
     */
    private static final int SPACING = 30;
    /**
     * number of columns.
     */
    private static final int COLUMNS = 2;

    /**
     * default constructor.
     * @param name the name of the panel
     */
    protected GenericParser(final String name) {
        this.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridLayout(0, COLUMNS, SPACING, SPACING));
        leftPanel = new JPanel(new GridLayout(0, 1));
        rightPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        this.add(new JLabel(name), BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * @param first  label
     * @param second item
     */
    final void addLeft(final Component first, final Component second) {
        leftPanel.add(new CouplePanel(first, second));

    }

    /**
     * @param first  label
     * @param second item
     */
    final void addRight(final Component first, final Component second) {
        rightPanel.add(new CouplePanel(first, second));
    }

};


