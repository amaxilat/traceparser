package eu.amaxilatis.java.traceparser.panels;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class CouplePanel extends JPanel {
    /**
     * @param first
     * @param second
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
