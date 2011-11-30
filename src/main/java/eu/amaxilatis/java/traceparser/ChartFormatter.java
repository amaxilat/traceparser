package eu.amaxilatis.java.traceparser;

import org.jfree.chart.JFreeChart;

import java.awt.Color;
import java.awt.BasicStroke;

/**
 * ChartFormatter
 */
public class ChartFormatter {

    /**
     *
     */
    private static ChartFormatter instance = null;

    /**
     * Constructor.
     */
    protected ChartFormatter() {
        // Exists only to defeat instantiation.
    }

    /**
     * @return
     */
    public ChartFormatter getInstance() {
        if (this.instance == null) {
            this.instance = new ChartFormatter();
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
    public static void setInstance(final ChartFormatter instance) {
        ChartFormatter.instance = instance;
    }

    /**
     * @param backgroundColor
     */
    public static void setBackgroundColor(final Color backgroundColor) {
        ChartFormatter.backgroundColor = backgroundColor;
    }

    /**
     * @param borderColor
     */
    public static void setBorderColor(final Color borderColor) {
        ChartFormatter.borderColor = borderColor;
    }

    /**
     * @param hasBorder
     */
    public static void setHasBorder(final boolean hasBorder) {
        ChartFormatter.hasBorder = hasBorder;
    }

    /**
     * @param hideLegend
     */
    public static void setHideLegend(final boolean hideLegend) {
        ChartFormatter.hideLegend = hideLegend;
    }

    /**
     * @param borderSize
     */
    public static void setBorderSize(final float borderSize) {
        ChartFormatter.borderSize = borderSize;
    }

    /**
     * @param hideTitle
     */
    public static void setHideTitle(final boolean hideTitle) {
        ChartFormatter.hideTitle = hideTitle;
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
        chart.setBackgroundPaint(ChartFormatter.backgroundColor);
        chart.setBorderPaint(ChartFormatter.borderColor);
        chart.setBorderVisible(ChartFormatter.hasBorder);
        if (ChartFormatter.hideLegend) {
            chart.removeLegend();
        }
        if ((ChartFormatter.hideTitle)) {
            chart.setTitle("");
        }
        chart.setBorderStroke(new BasicStroke(ChartFormatter.borderSize));

        return chart;
    }
}
