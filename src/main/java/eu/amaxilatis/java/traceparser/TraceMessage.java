package eu.amaxilatis.java.traceparser;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/10/11
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TraceMessage {
    private String text;
    private String urn;
    private long time;
    private String level;


    static String urnText = "Source [";
    static String textText = "Text [";
    static String dateText = "Time [";
    static String levelText = "Level [";
    static String endText = "]";


    public TraceMessage(String strLine) {
        urn = extractNodeUrn(strLine);
        text = extractText(strLine);
        time = extractDate(strLine);
        level = extractLevel(strLine);
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
        final int date_stop = line.indexOf("+", date_start);
        final String date = line.substring(date_start, date_stop);
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S");
        try {
            final Date d = f.parse(date);
            return d.getTime();
        } catch (Exception e) {
        }
        return -1;
    }

    String extractNodeUrn(String line) {
        final int nodeurn_start = line.indexOf(urnText) + urnText.length();
        final int nodeurn_stop = line.indexOf(endText, nodeurn_start);
        return line.substring(nodeurn_start, nodeurn_stop);
    }
}
