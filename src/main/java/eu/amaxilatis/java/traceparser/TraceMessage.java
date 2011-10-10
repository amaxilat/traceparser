package eu.amaxilatis.java.traceparser;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/10/11
 * Time: 5:09 PM
 */
public class TraceMessage {
    private  String text = null;
    private  String urn = null;
    private  long time = 0;
    private  String level = null;

    private static final Logger log = Logger.getLogger(TraceMessage.class);


    private static final String urnText = "Source [";
    private static final String textText = "Text [";
    private static final String dateText = "Time [";
    private static final String levelText = "Level [";
    private static final String endText = "]";


    public TraceMessage(String strLine) {
        try {
            urn = extractNodeUrn(strLine);
            text = extractText(strLine);
            time = extractDate(strLine);
            level = extractLevel(strLine);
        } catch (Exception e) {
            log.error("Error: " + e.toString());
            e.printStackTrace();
        }
    }

    public long time() {
        return time;
    }

    public String text() {
        return text;
    }

    public String urn() {
        return urn;
    }

    public String level() {
        return level;
    }


    String extractText(String line) {
        final int text_start = line.indexOf(textText) + textText.length();
        final int text_stop = line.indexOf(endText, text_start);
        return line.substring(text_start, text_stop);
    }

    String extractLevel(String line) {
        final int level_start = line.indexOf(levelText) + levelText.length();
        final int level_stop = line.indexOf(endText, level_start);
        return line.substring(level_start, level_stop);
    }

    long extractDate(String line) {
        final int date_start = line.indexOf(dateText) + dateText.length();

        //final int date_stop = line.indexOf("+", date_start);
        final int date_stop = date_start + 23;
        final String date = line.substring(date_start, date_stop);
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S");
        int time_offset = 0;
        if (line.substring(date_stop).contains("+")) {
            final int offset_start = line.indexOf("+", date_stop) + 1;
            time_offset = -1 * Integer.parseInt(line.substring(offset_start, offset_start + 2));
            //TraceParserApp.log.info(time_offset);
        } else if (line.substring(date_stop).contains("-")) {
            final int offset_start = line.indexOf("-", date_stop) + 1;
            time_offset = Integer.parseInt(line.substring(offset_start, offset_start + 2));
            //TraceParserApp.log.info(line.substring(offset_start, offset_start + 2) +" - "+time_offset);
        }
        try {
            final Date d = f.parse(date);
            return d.getTime() + time_offset * 60 * 60 * 1000;
        } catch (Exception ignored) {
        }


        return -1;
    }

    String extractNodeUrn(String line) {
        final int nodeurn_start = line.indexOf(urnText) + urnText.length();
        final int nodeurn_stop = line.indexOf(endText, nodeurn_start);
        return line.substring(nodeurn_start, nodeurn_stop);
    }
}
