package eu.amaxilatis.java.traceparser;

import org.jfree.chart.JFreeChart;

import java.awt.Color;
import java.awt.BasicStroke;

/**
 * ChartFormater
 */
public class ChartFormater {

    private static ChartFormater instance = null;

    protected ChartFormater() {
        // Exists only to defeat instantiation.
    }

    /**
     *
     * @return
     */
    public static ChartFormater getInstance() {
        if (instance == null) {
            instance = new ChartFormater();
        }
        return instance;
    }

    private static Color backgroundColor = Color.white;
    private static Color borderColor = Color.black;
    private static boolean hasBorder = false;
    private static boolean hideLegend = false;
    private static float borderSize = 1;
    private static boolean hideTitle;

    /**
     * @param instance
     */
    public static void setInstance(ChartFormater instance) {
        ChartFormater.instance = instance;
    }

    /**
     * @param backgroundColor
     */
    public static void setBackgroundColor(Color backgroundColor) {
        ChartFormater.backgroundColor = backgroundColor;
    }

    /**
     * @param borderColor
     */
    public static void setBorderColor(Color borderColor) {
        ChartFormater.borderColor = borderColor;
    }

    /**
     * @param hasBorder
     */
    public static void setHasBorder(boolean hasBorder) {
        ChartFormater.hasBorder = hasBorder;
    }

    /**
     * @param hideLegend
     */
    public static void setHideLegend(boolean hideLegend) {
        ChartFormater.hideLegend = hideLegend;
    }

    /**
     * @param borderSize
     */
    public static void setBorderSize(float borderSize) {
        ChartFormater.borderSize = borderSize;
    }

    /**
     * @param hideTitle
     */
    public static void setHideTitle(boolean hideTitle) {
        ChartFormater.hideTitle = hideTitle;
    }

    /**
     * @return
     */
    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @return
     */
    public static Color getBorderColor() {
        return borderColor;
    }

    /**
     * @return
     */
    public static boolean getHasBorder() {
        return hasBorder;
    }

    /**
     * @return
     */
    public static boolean getHideLegend() {
        return hideLegend;
    }

    /**
     * @return
     */
    public static float getBorderSize() {
        return borderSize;
    }

    /**
     * @return
     */
    public static boolean getHideTitle() {
        return hideTitle;
    }

    /**
     * transforms a chart to fit the options of this class
     *
     * @param chart the chart to be formated
     * @return the formated chart
     */
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
