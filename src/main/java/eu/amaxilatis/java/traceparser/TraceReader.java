package eu.amaxilatis.java.traceparser;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/2/11
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class TraceReader extends Observable implements Runnable {


    private TraceFile file;

    static String urnText = "Source [";
    static String textText = "Text [";
    static String dateText = "Time [";
    static String levelText = "Level [";
    static String endText = "]";
    private Logger log;

    public TraceReader(TraceFile file_) {
        log = TraceParserApp.log;
        file = file_;
    }


    public void run() {
        int count = 0;

        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(file.filename());
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                //log.debug(strLine);
                //log.debug(extractNodeUrn(strLine) + "@" + extractDate(strLine) + ":" + extractText(strLine));
                notifyObservers(new TraceMessage(strLine));
                notifyObservers();
                this.setChanged();

                count++;
                //Thread.sleep(100);
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            log.error("Error: " + e.toString() + " line: " + count);
            e.printStackTrace();
        }
    }

    String extractNodeUrn(String line) {
        final int nodeurn_start = line.indexOf(urnText) + urnText.length();
        final int nodeurn_stop = line.indexOf(endText, nodeurn_start);
        return line.substring(nodeurn_start, nodeurn_stop);
    }

    String extractText(String line) {
        final int text_start = line.indexOf(textText) + textText.length();
        final int text_stop = line.indexOf(endText, text_start);
        return line.substring(text_start, text_stop);
    }

    long extractDate(String line) {
        final int date_start = line.indexOf(dateText) + dateText.length();
        final int date_stop = line.indexOf("+02:00" + endText, date_start);
        final String date = line.substring(date_start, date_stop);
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S");
        try {
            final Date d = f.parse(date);
            return d.getTime();
        } catch (Exception e) {
        }
        return -1;
    }
}
