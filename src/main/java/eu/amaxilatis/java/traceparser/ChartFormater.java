package eu.amaxilatis.java.traceparser;

import org.jfree.chart.JFreeChart;

import java.awt.*;


public class ChartFormater {

    private static ChartFormater instance = null;

    protected ChartFormater() {
        // Exists only to defeat instantiation.
    }

    public static ChartFormater getInstance() {
        if (instance == null) {
            instance = new ChartFormater();
        }
        return instance;
    }


    public static Color backgroundColor = Color.white;
    public static Color borderColor = Color.black;
    public static boolean hasBorder = false;
    public static boolean hideLegend = false;
    public static float borderSize = 1;
    public static boolean hideTitle;

    public static JFreeChart transformChart(final JFreeChart chart) {
        chart.setBackgroundPaint(ChartFormater.backgroundColor);
        chart.setBorderPaint(ChartFormater.borderColor);
        chart.setBorderVisible(ChartFormater.hasBorder);
        if (ChartFormater.hideLegend) {
            chart.removeLegend();
        }
        if ((ChartFormater.hideTitle)) {
            chart.setTitle("");
        }
        chart.setBorderStroke(new BasicStroke(ChartFormater.borderSize));

        return chart;

    }
}
