package eu.amaxilatis.java.traceparser.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 10/10/11
 * Time: 8:03 PM
 */
public class PlotterControlPanel extends JPanel {
    public PlotterControlPanel() {

        this.setLayout(new BorderLayout(30, 30));

        this.add(new JLabel("Plot options"), BorderLayout.NORTH);
    }
}
