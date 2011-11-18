package eu.amaxilatis.java.traceparser.panels;

import eu.amaxilatis.java.traceparser.ChartFormater;

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
    private final static JButton UPDATE_BUTTON = new JButton("Apply new Settings");
    private final static JButton RESET_BUTTON = new JButton("Reset");
    private final static JTextField BG_COLOR = new JTextField("default");
    private final static JTextField BORDER_COLOR = new JTextField("default");
    private final static JCheckBox HAS_BORDER = new JCheckBox("", ChartFormater.isBorder());
    private final static JCheckBox HIDE_LEGEND = new JCheckBox("", ChartFormater.isHideLegend());
    private final static JCheckBox HIDE_TITLE = new JCheckBox("", ChartFormater.isHideTitle());
    private final static JSpinner BORDER_SIZE = new JSpinner();

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
        BG_COLOR.setBorder(BorderFactory.createLineBorder(ChartFormater.getBackgroundColor(), 5));
        leftPanel.add(new CouplePanel(new JLabel("Bg color"), BG_COLOR));
        //bordeColor
        BORDER_COLOR.setBorder(BorderFactory.createLineBorder(ChartFormater.getBorderColor(), 5));
        leftPanel.add(new CouplePanel(new JLabel("Border color"), BORDER_COLOR));
        //bordeColor
        BORDER_SIZE.setValue(ChartFormater.getBorderSize());
        leftPanel.add(new CouplePanel(new JLabel("Border size"), BORDER_SIZE));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Use Border"), HAS_BORDER));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Hide Legend"), HIDE_LEGEND));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Hide Title"), HIDE_TITLE));
    }

    /**
     *
     * @param actionEvent
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(UPDATE_BUTTON)) {
            final Color newBGColor = new Color(getRed(BG_COLOR.getText()), getGreen(BG_COLOR.getText()), getBlue(BG_COLOR.getText()));
            BG_COLOR.setBorder(BorderFactory.createLineBorder(newBGColor, 5));
            ChartFormater.setBackgroundColor(newBGColor);
            final Color newBorderColor = new Color(getRed(BORDER_COLOR.getText()), getGreen(BORDER_COLOR.getText()), getBlue(BORDER_COLOR.getText()));
            BORDER_COLOR.setBorder(BorderFactory.createLineBorder(newBorderColor, 5));
            ChartFormater.setBorderColor(newBorderColor);
            ChartFormater.setBorderSize(Float.parseFloat(BORDER_SIZE.getValue().toString()));
            ChartFormater.setHasBorder(HAS_BORDER.isSelected());
            ChartFormater.setHideLegend(HIDE_LEGEND.isSelected());
            ChartFormater.setHideTitle(HIDE_TITLE.isSelected());
        }
    }

    /**
     *
     * @param text
     * @return
     */
    private int getRed(final String text) {
        return Integer.parseInt(text.split(",")[0]);
    }

    /**
     *
     * @param text
     * @return
     */
    private int getGreen(final String text) {
        return Integer.parseInt(text.split(",")[1]);
    }

    /**
     *
     * @param text
     * @return
     */
    private int getBlue(final String text) {
        return Integer.parseInt(text.split(",")[2]);
    }

//    private String color2string(final Color color) {
//        final String red, green, blue;
//        red = Integer.toString(color.getRed());
//        green = Integer.toString(color.getGreen());
//        blue = Integer.toString(color.getBlue());
//        return red + "," + green + "," + blue;
//    }
}
