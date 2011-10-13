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

public abstract class AbstractParser implements Observer {

    public abstract ChartPanel getPlot(boolean has_title, boolean aggregate, String title, String xlabel, String ylabel);

    public abstract ChartPanel getPlot();

    public abstract XYSeries[] getSeries();

    public abstract XYSeries[] getSeries_aggregate();

    public abstract void setFile(TraceFile file);

    public abstract void setTemplate(String template);

    public abstract String getTemplate();
}

