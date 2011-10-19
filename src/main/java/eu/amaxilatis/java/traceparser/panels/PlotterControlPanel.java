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
    private JButton updateButton;
    private JTextField chartBGColor;
    private JButton resetButton;
    private JTextField chartBorderColor;
    private JCheckBox chartHasBorder;
    private JCheckBox chartHideLegend;
    private JSpinner chartBorderSize;
    private JCheckBox chartHideTitle;

    public PlotterControlPanel() {

        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new GridLayout(0, 2, 30, 30));
        JPanel leftmainpanel = new JPanel(new GridLayout(0, 1));
        JPanel rightmainpanel = new JPanel(new GridLayout(0, 1));
        mainpanel.add(leftmainpanel);
        mainpanel.add(rightmainpanel);

        this.add(new JLabel("Plot options"), BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        updateButton = new JButton("Apply new Settings");
        updateButton.addActionListener(this);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        rightmainpanel.add(new couplePanel(updateButton, resetButton));

        leftmainpanel.add(new JLabel("Setting for the graphics of all generated plots."));
        leftmainpanel.add(new JLabel("All color settings are in RGB format. (0-255) separate with ,"));


//bgcolor
        chartBGColor = new JTextField(color2string(ChartFormater.BackgroundColor));
        chartBGColor.setBorder(BorderFactory.createLineBorder(ChartFormater.BackgroundColor, 5));
        leftmainpanel.add(new couplePanel(new JLabel("Bg color"), chartBGColor));
//bordeColor
        chartBorderColor = new JTextField(color2string(ChartFormater.BorderColor));
        chartBorderColor.setBorder(BorderFactory.createLineBorder(ChartFormater.BorderColor, 5));
        leftmainpanel.add(new couplePanel(new JLabel("Border color"), chartBorderColor));
//bordeColor
        chartBorderSize = new JSpinner();
        chartBorderSize.setValue(ChartFormater.BorderSize);
        leftmainpanel.add(new couplePanel(new JLabel("Border size"), chartBorderSize));

//use border
        chartHasBorder = new JCheckBox("", ChartFormater.HasBorder);
        rightmainpanel.add(new couplePanel(new JLabel("Use Border"), chartHasBorder));
//use border
        chartHideLegend = new JCheckBox("", ChartFormater.HideLegend);
        rightmainpanel.add(new couplePanel(new JLabel("Hide Legend"), chartHideLegend));

        //use border
        chartHideTitle = new JCheckBox("", ChartFormater.HideTitle);
        rightmainpanel.add(new couplePanel(new JLabel("Hide Title"), chartHideTitle));


    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(updateButton)) {

            final Color newBGColor = new Color(getRed(chartBGColor.getText()), getGreen(chartBGColor.getText()), getBlue(chartBGColor.getText()));
            chartBGColor.setBorder(BorderFactory.createLineBorder(newBGColor, 5));
            ChartFormater.BackgroundColor = newBGColor;
            final Color newBorderColor = new Color(getRed(chartBorderColor.getText()), getGreen(chartBorderColor.getText()), getBlue(chartBorderColor.getText()));
            chartBorderColor.setBorder(BorderFactory.createLineBorder(newBorderColor, 5));
            ChartFormater.BorderColor = newBorderColor;
            ChartFormater.BorderSize = Float.parseFloat(chartBorderSize.getValue().toString());

            ChartFormater.HasBorder = chartHasBorder.isSelected();
            ChartFormater.HideLegend = chartHideLegend.isSelected();
            ChartFormater.HideTitle = chartHideTitle.isSelected();


        }
    }

    private int getRed(String text) {
        return Integer.parseInt(text.split(",")[0]);
    }

    private int getGreen(String text) {
        return Integer.parseInt(text.split(",")[1]);
    }

    private int getBlue(String text) {
        return Integer.parseInt(text.split(",")[2]);
    }

    private String color2string(Color c) {
        final String red, green, blue;
        red = Integer.toString(c.getRed());
        green = Integer.toString(c.getGreen());
        blue = Integer.toString(c.getBlue());
        return red + "," + green + "," + blue;
    }
}
