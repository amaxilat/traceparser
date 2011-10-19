package eu.amaxilatis.java.traceparser.panels;

import javax.swing.*;
import java.awt.*;

public class couplePanel extends JPanel {
    public couplePanel(Component a, Component b) {

        this.setLayout(new FlowLayout());
        Dimension d = new Dimension(200, 50);
        this.setPreferredSize(d);
        this.setMaximumSize(d);
        this.setMinimumSize(d);
        a.setPreferredSize(d);
        b.setPreferredSize(d);
        this.add(a);
        this.add(b);
    }
}