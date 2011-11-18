package eu.amaxilatis.java.traceparser;

import org.jfree.chart.JFreeChart;

import java.awt.Color;
import java.awt.BasicStroke;

/**
 * ChartFormater
 */
public class ChartFormater {

    /**
     *
     */
    private static ChartFormater instance = null;

    /**
     * Constructor.
     */
    protected ChartFormater() {
        // Exists only to defeat instantiation.
    }

    /**
     * @return
     */
    public ChartFormater getInstance() {
        if (this.instance == null) {
            this.instance = new ChartFormater();
        }
        return this.instance;
    }

    /**
     *
     */
    private static Color backgroundColor = Color.white;
    /**
     *
     */
    private static Color borderColor = Color.black;
    /**
     *
     */
    private static boolean hasBorder = false;
    /**
     *
     */
    private static boolean hideLegend = false;
    /**
     *
     */
    private static float borderSize = 1;
    /**
     *
     */
    private static boolean hideTitle;

    /**
     * @param instance
     */
    public static void setInstance(final ChartFormater instance) {
        ChartFormater.instance = instance;
    }

    /**
     * @param backgroundColor
     */
    public static void setBackgroundColor(final Color backgroundColor) {
        ChartFormater.backgroundColor = backgroundColor;
    }

    /**
     * @param borderColor
     */
    public static void setBorderColor(final Color borderColor) {
        ChartFormater.borderColor = borderColor;
    }

    /**
     * @param hasBorder
     */
    public static void setHasBorder(final boolean hasBorder) {
        ChartFormater.hasBorder = hasBorder;
    }

    /**
     * @param hideLegend
     */
    public static void setHideLegend(final boolean hideLegend) {
        ChartFormater.hideLegend = hideLegend;
    }

    /**
     * @param borderSize
     */
    public static void setBorderSize(final float borderSize) {
        ChartFormater.borderSize = borderSize;
    }

    /**
     * @param hideTitle
     */
    public static void setHideTitle(final boolean hideTitle) {
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
    public static boolean isBorder() {
        return hasBorder;
    }

    /**
     * @return
     */
    public static boolean isHideLegend() {
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
    public static boolean isHideTitle() {
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
