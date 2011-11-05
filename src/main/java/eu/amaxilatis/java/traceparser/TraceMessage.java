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
    private String text;
    private String urn = null;
    private long time = 0;
    private String level = null;

    private static final Logger LOGGER = Logger.getLogger(TraceMessage.class);


    private static final String URNTEXT = "Source [";
    private static final String TEXTTEXT = "Text [";
    private static final String DATETEXT = "Time [";
    private static final String LEVELTEXT = "Level [";
    private static final String ENDTEXT = "]";


    public TraceMessage(final String strLine) {

        init(strLine);

    }

    private void init(final String strLine) {
        try {
            urn = extractNodeUrn(strLine);
            text = extractText(strLine);
            time = extractDate(strLine);
            level = extractLevel(strLine);
        } catch (Exception e) {
            LOGGER.error("Error: " + e);
        }
    }

    public long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public String getUrn() {
        return urn;
    }

    public String getLevel() {
        return level;
    }


    String extractText(final String line) {
        final int text_start = line.indexOf(TEXTTEXT) + TEXTTEXT.length();
        final int text_stop = line.indexOf(ENDTEXT, text_start);
        return line.substring(text_start, text_stop);
    }

    String extractLevel(final String line) {
        final int level_start = line.indexOf(LEVELTEXT) + LEVELTEXT.length();
        final int level_stop = line.indexOf(ENDTEXT, level_start);
        return line.substring(level_start, level_stop);
    }

    long extractDate(final String line) {
        final int date_start = line.indexOf(DATETEXT) + DATETEXT.length();

        //final int date_stop = line.indexOf("+", date_start);
        final int date_stop = date_start + 23;
        final String date = line.substring(date_start, date_stop);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S");
        int time_offset = 0;
        if (line.substring(date_stop).contains("+")) {
            final int offset_start = line.indexOf('+', date_stop) + 1;
            time_offset = -1 * Integer.parseInt(line.substring(offset_start, offset_start + 2));
        } else if (line.substring(date_stop).contains("-")) {
            final int offset_start = line.indexOf('-', date_stop) + 1;
            time_offset = Integer.parseInt(line.substring(offset_start, offset_start + 2));
        }
        try {
            final Date dateParsed = dateFormat.parse(date);
            return dateParsed.getTime() + time_offset * 60 * 60 * 1000;
        } catch (Exception e) {
            LOGGER.error(e);
        }


        return -1;
    }

    String extractNodeUrn(final String line) {
        final int nodeurn_start = line.indexOf(URNTEXT) + URNTEXT.length();
        final int nodeurn_stop = line.indexOf(ENDTEXT, nodeurn_start);
        return line.substring(nodeurn_start, nodeurn_stop);
    }
}
