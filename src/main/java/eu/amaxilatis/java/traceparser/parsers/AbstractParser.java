package eu.amaxilatis.java.traceparser.parsers;

import eu.amaxilatis.java.traceparser.TraceFile;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;

import java.util.Observer;


/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/2/11
 * Time: 12:33 PM
 */

public interface AbstractParser {

    public ChartPanel getPlot(boolean has_title, boolean aggregate, String title, String xlabel, String ylabel);

    XYSeries[] getSeries();

    XYSeries[] getSeries_aggregate();

}

