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
    final private JButton updateButton;
    final private JTextField chartBGColor;
    final private JButton resetButton;
    final private JTextField chartBorderColor;
    final private JCheckBox chartHasBorder;
    final private JCheckBox chartHideLegend;
    final private JSpinner chartBorderSize;
    final private JCheckBox chartHideTitle;

    public PlotterControlPanel() {

        this.setLayout(new BorderLayout());

        final JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        final JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        final JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel("Plot options"), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        updateButton = new JButton("Apply new Settings");
        updateButton.addActionListener(this);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        rightmainpanel.add(new CouplePanel(updateButton, resetButton));

        leftmainpanel.add(new JLabel("Setting for the graphics of all generated plots."));
        leftmainpanel.add(new JLabel("All color settings are in RGB format. (0-255) separate with ,"));


//bgcolor
        chartBGColor = new JTextField(color2string(ChartFormater.getBackgroundColor()));
        chartBGColor.setBorder(BorderFactory.createLineBorder(ChartFormater.getBackgroundColor(), 5));
        leftmainpanel.add(new CouplePanel(new JLabel("Bg color"), chartBGColor));
//bordeColor
        chartBorderColor = new JTextField(color2string(ChartFormater.getBorderColor()));
        chartBorderColor.setBorder(BorderFactory.createLineBorder(ChartFormater.getBorderColor(), 5));
        leftmainpanel.add(new CouplePanel(new JLabel("Border color"), chartBorderColor));
//bordeColor
        chartBorderSize = new JSpinner();
        chartBorderSize.setValue(ChartFormater.getBorderSize());
        leftmainpanel.add(new CouplePanel(new JLabel("Border size"), chartBorderSize));

//use border
        chartHasBorder = new JCheckBox("", ChartFormater.getHasBorder());
        rightmainpanel.add(new CouplePanel(new JLabel("Use Border"), chartHasBorder));
//use border
        chartHideLegend = new JCheckBox("", ChartFormater.getHideLegend());
        rightmainpanel.add(new CouplePanel(new JLabel("Hide Legend"), chartHideLegend));

        //use border
        chartHideTitle = new JCheckBox("", ChartFormater.getHideTitle());
        rightmainpanel.add(new CouplePanel(new JLabel("Hide Title"), chartHideTitle));


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
