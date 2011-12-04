package eu.amaxilatis.java.traceparser.panels;

import eu.amaxilatis.java.traceparser.ChartFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 10/10/11
 * Time: 8:03 PM
 */
public class PlotterControlPanel extends JPanel implements ActionListener {
    /**
     * button.
     */
    private static final JButton UPDATE_BUTTON = new JButton("Apply new Settings");
    /**
     * button.
     */
    private static final JButton RESET_BUTTON = new JButton("Reset");
    /**
     * field.
     */
    private static final JTextField BG_COLOR = new JTextField("default");
    /**
     * field.
     */
    private static final JTextField BORDER_COLOR = new JTextField("default");
    /**
     * checkbox.
     */
    private static final JCheckBox HAS_BORDER = new JCheckBox("", ChartFormatter.isBorder());
    /**
     * checkbox.
     */
    private static final JCheckBox HIDE_LEGEND = new JCheckBox("", ChartFormatter.isHideLegend());
    /**
     * checkbox.
     */
    private static final JCheckBox HIDE_TITLE = new JCheckBox("", ChartFormatter.isHideTitle());
    /**
     * size.
     */
    private static final JSpinner BORDER_SIZE = new JSpinner();

    /**
     * Constructor.
     */
    public PlotterControlPanel() {
        this.setLayout(new BorderLayout());

        final JPanel mainPanel = new JPanel(new GridLayout(0, 2, 30, 30));
        final JPanel leftPanel = new JPanel(new GridLayout(0, 1));
        final JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        this.add(new JLabel("Plot options"), BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);

        UPDATE_BUTTON.addActionListener(this);

        RESET_BUTTON.addActionListener(this);
        rightPanel.add(new CouplePanel(UPDATE_BUTTON, RESET_BUTTON));

        leftPanel.add(new JLabel("Setting for the graphics of all generated plots."));
        leftPanel.add(new JLabel("All color settings are in RGB format. (0-255) separate with ,"));


        //bgcolor
        BG_COLOR.setBorder(BorderFactory.createLineBorder(ChartFormatter.getBackgroundColor(), 5));
        leftPanel.add(new CouplePanel(new JLabel("Bg color"), BG_COLOR));
        //bordeColor
        BORDER_COLOR.setBorder(BorderFactory.createLineBorder(ChartFormatter.getBorderColor(), 5));
        leftPanel.add(new CouplePanel(new JLabel("Border color"), BORDER_COLOR));
        //bordeColor
        BORDER_SIZE.setValue(ChartFormatter.getBorderSize());
        leftPanel.add(new CouplePanel(new JLabel("Border size"), BORDER_SIZE));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Use Border"), HAS_BORDER));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Hide Legend"), HIDE_LEGEND));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Hide Title"), HIDE_TITLE));
    }

    /**
     * event listener.
     *
     * @param actionEvent action happened
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(UPDATE_BUTTON)) {
            final Color newBGColor = new Color(getRed(BG_COLOR.getText()), getGreen(BG_COLOR.getText()), getBlue(BG_COLOR.getText()));
            BG_COLOR.setBorder(BorderFactory.createLineBorder(newBGColor, 5));
            ChartFormatter.setBackgroundColor(newBGColor);
            final Color newBorderColor = new Color(getRed(BORDER_COLOR.getText()), getGreen(BORDER_COLOR.getText()), getBlue(BORDER_COLOR.getText()));
            BORDER_COLOR.setBorder(BorderFactory.createLineBorder(newBorderColor, 5));
            ChartFormatter.setBorderColor(newBorderColor);
            ChartFormatter.setBorderSize(Float.parseFloat(BORDER_SIZE.getValue().toString()));
            ChartFormatter.setHasBorder(HAS_BORDER.isSelected());
            ChartFormatter.setHideLegend(HIDE_LEGEND.isSelected());
            ChartFormatter.setHideTitle(HIDE_TITLE.isSelected());
        }
    }

    /**
     * get the rgb code for red.
     *
     * @param text value as string
     * @return value as int
     */
    private int getRed(final String text) {
        return Integer.parseInt(text.split(",")[0]);
    }

    /**
     * get the rgb code for green.
     *
     * @param text value as string
     * @return value as int
     */
    private int getGreen(final String text) {
        return Integer.parseInt(text.split(",")[1]);
    }

    /**
     * get the rgb code for blue.
     *
     * @param text value as string
     * @return value as int
     */
    private int getBlue(final String text) {
        return Integer.parseInt(text.split(",")[2]);
    }
}
