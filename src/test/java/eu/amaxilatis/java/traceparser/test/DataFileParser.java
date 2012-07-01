package eu.amaxilatis.java.traceparser.test;

import eu.amaxilatis.java.traceparser.ChartFormatter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 5/14/12
 * Time: 9:55 PM
 */
public class DataFileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFileParser.class);
    static XYSeries[] xySerieses = new XYSeries[2];

    public static void main(final String[] args) {
        xySerieses[0] = new XYSeries("Fixed-ND");
        xySerieses[1] = new XYSeries("Adaptive-ND");
        String strLine;
        try {
            // Open the file that is the first
            // command line parameter
            final FileInputStream stream = new FileInputStream("/home/amaxilatis/data3");
            // Get the object of DataInputStream
            final DataInputStream dataInputStream = new DataInputStream(stream);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
            //Read File Line By Line

            while ((strLine = reader.readLine()) != null) {
                if (!"".equals(strLine)) {
                    LOGGER.info(strLine);
                    String[] parts = strLine.split("\t");
                    xySerieses[0].add(Double.valueOf(parts[0]), Double.valueOf(parts[1]));
                    xySerieses[1].add(Double.valueOf(parts[0]), Double.valueOf(parts[2]));

                }
            }
            //Close the input stream
            dataInputStream.close();
        } catch (Exception e) {//Catch exception if any

        }



        ChartPanel chart = getPlot();
        JFrame jf = new JFrame();
        jf.add(chart);
        jf.pack();
        jf.setVisible(true);


    }


    /**
     * @return
     */
    private static ChartPanel getPlot() {
        final XYSeriesCollection dataset = new XYSeriesCollection();


        for (final XYSeries clustersSery : xySerieses) {
            dataset.addSeries(clustersSery);
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Nodes",
                "# of Messages",
                dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.getXYPlot().getRenderer().setSeriesStroke( 0,
                new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {2.0f, 6.0f}, 0.0f
                ));
        final JFreeChart chartTransformed = ChartFormatter.transformChart(chart);
        return new ChartPanel(chartTransformed);
    }
}
