package eu.amaxilatis.java.traceparser;

import org.jfree.chart.JFreeChart;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 10/19/11
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartFormater {

    public static Color BackgroundColor = Color.white;
    public static Color BorderColor = Color.black;
    public static boolean HasBorder = false;
    public static boolean HideLegend = false;
    public static float BorderSize = 1;
    public static boolean HideTitle;

    public static JFreeChart transformChart(JFreeChart chart) {
        chart.setBackgroundPaint(ChartFormater.BackgroundColor);
        chart.setBorderPaint(ChartFormater.BorderColor);
        chart.setBorderVisible(ChartFormater.HasBorder);
        if (ChartFormater.HideLegend) {
            chart.removeLegend();
        }
        if ((ChartFormater.HideTitle)) {
            chart.setTitle("");
        }
        chart.setBorderStroke(new BasicStroke(ChartFormater.BorderSize));

        return chart;

    }
}
