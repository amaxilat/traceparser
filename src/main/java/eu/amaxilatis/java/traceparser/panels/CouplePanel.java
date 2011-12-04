package eu.amaxilatis.java.traceparser.panels;

import javax.swing.*;
import java.awt.*;

/**
 * A class use to add 2 Components in a row of a panel.
 */
public class CouplePanel extends JPanel {

    /**
     * constructor.
     *
     * @param first  the first component
     * @param second the second component
     */
    public CouplePanel(final Component first, final Component second) {

        this.setLayout(new FlowLayout());
        final Dimension dimension = new Dimension(200, 50);
        this.setPreferredSize(dimension);
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
        first.setPreferredSize(dimension);
        second.setPreferredSize(dimension);
        this.add(first);
        this.add(second);
    }
}
