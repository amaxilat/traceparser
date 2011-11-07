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
    private final static JButton updateButton = new JButton("Apply new Settings");
    private final static JButton resetButton = new JButton("Reset");
    private final static JTextField chartBGColor = new JTextField("default");
    private final static JTextField chartBorderColor = new JTextField("default");
    private final static JCheckBox chartHasBorder = new JCheckBox("", ChartFormater.getHasBorder());
    private final static JCheckBox chartHideLegend = new JCheckBox("", ChartFormater.getHideLegend());
    private final static JCheckBox chartHideTitle = new JCheckBox("", ChartFormater.getHideTitle());
    private final static JSpinner chartBorderSize = new JSpinner();

    public PlotterControlPanel() {
        this.setLayout(new BorderLayout());

        final JPanel mainPanel = new JPanel(new GridLayout(0, 2, 30, 30));
        final JPanel leftPanel = new JPanel(new GridLayout(0, 1));
        final JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        this.add(new JLabel("Plot options"), BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);

        updateButton.addActionListener(this);

        resetButton.addActionListener(this);
        rightPanel.add(new CouplePanel(updateButton, resetButton));

        leftPanel.add(new JLabel("Setting for the graphics of all generated plots."));
        leftPanel.add(new JLabel("All color settings are in RGB format. (0-255) separate with ,"));


        //bgcolor
        chartBGColor.setBorder(BorderFactory.createLineBorder(ChartFormater.getBackgroundColor(), 5));
        leftPanel.add(new CouplePanel(new JLabel("Bg color"), chartBGColor));
        //bordeColor
        chartBorderColor.setBorder(BorderFactory.createLineBorder(ChartFormater.getBorderColor(), 5));
        leftPanel.add(new CouplePanel(new JLabel("Border color"), chartBorderColor));
        //bordeColor
        chartBorderSize.setValue(ChartFormater.getBorderSize());
        leftPanel.add(new CouplePanel(new JLabel("Border size"), chartBorderSize));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Use Border"), chartHasBorder));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Hide Legend"), chartHideLegend));
        //use border
        rightPanel.add(new CouplePanel(new JLabel("Hide Title"), chartHideTitle));
    }

    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(updateButton)) {
            final Color newBGColor = new Color(getRed(chartBGColor.getText()), getGreen(chartBGColor.getText()), getBlue(chartBGColor.getText()));
            chartBGColor.setBorder(BorderFactory.createLineBorder(newBGColor, 5));
            ChartFormater.setBackgroundColor(newBGColor);
            final Color newBorderColor = new Color(getRed(chartBorderColor.getText()), getGreen(chartBorderColor.getText()), getBlue(chartBorderColor.getText()));
            chartBorderColor.setBorder(BorderFactory.createLineBorder(newBorderColor, 5));
            ChartFormater.setBorderColor(newBorderColor);
            ChartFormater.setBorderSize(Float.parseFloat(chartBorderSize.getValue().toString()));
            ChartFormater.setHasBorder(chartHasBorder.isSelected());
            ChartFormater.setHideLegend(chartHideLegend.isSelected());
            ChartFormater.setHideTitle(chartHideTitle.isSelected());
        }
    }

    private int getRed(final String text) {
        return Integer.parseInt(text.split(",")[0]);
    }

    private int getGreen(final String text) {
        return Integer.parseInt(text.split(",")[1]);
    }

    private int getBlue(final String text) {
        return Integer.parseInt(text.split(",")[2]);
    }

    private String color2string(final Color color) {
        final String red, green, blue;
        red = Integer.toString(color.getRed());
        green = Integer.toString(color.getGreen());
        blue = Integer.toString(color.getBlue());
        return red + "," + green + "," + blue;
    }
}
