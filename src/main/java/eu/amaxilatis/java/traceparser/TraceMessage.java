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
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(TraceMessage.class);
    /**
     *
     */
    private static final String URNTEXT = "Source [";
    /**
     *
     */
    private static final String TEXTTEXT = "Text [";
    /**
     *
     */
    private static final String DATETEXT = "Time [";
    /**
     *
     */
    private static final String LEVELTEXT = "Level [";
    /**
     *
     */
    private static final String ENDTEXT = "]";

    /**
     * text of message.
     */
    private String text;
    /**
     * urn of node.
     */
    private String urn = null;
    /**
     * timestamp.
     */
    private long time = 0;
    /**
     * level of message.
     */
    private String level = null;
    /**
     *
     */
    private static final int MILLIS_IN_HOUR = 60 * 60 * 1000;


    /**
     * @param strLine the message text
     */
    public TraceMessage(final String strLine) {

        init(strLine);

    }

    /**
     * @param strLine the string message
     */
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

    /**
     * @return the message timestamp.
     */
    public final long getTime() {
        return time;
    }

    /**
     * @return the message text.
     */
    public final String getText() {
        return text;
    }

    /**
     * @return the experiment node urn.
     */
    public final String getUrn() {
        return urn;
    }

    /**
     * @return the message level type.
     */
    public final String getLevel() {
        return level;
    }


    /**
     * @param line message received
     * @return
     */
    private String extractText(final String line) {
        final int textStart = line.indexOf(TEXTTEXT) + TEXTTEXT.length();
        final int textStop = line.indexOf(ENDTEXT, textStart);
        return line.substring(textStart, textStop);
    }

    /**
     * @param line
     * @return
     */
    private String extractLevel(final String line) {
        final int levelStart = line.indexOf(LEVELTEXT) + LEVELTEXT.length();
        final int levelStop = line.indexOf(ENDTEXT, levelStart);
        return line.substring(levelStart, levelStop);
    }

    /**
     * @param line
     * @return
     */
    private long extractDate(final String line) {
        final int dateStart = line.indexOf(DATETEXT) + DATETEXT.length();

        //final int dateStop = line.indexOf("+", dateStart);
        final int dateStop = dateStart + 23;
        final String date = line.substring(dateStart, dateStop);
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S");
        int timeOffset = 0;
        if (line.substring(dateStop).contains("+")) {
            final int offsetStart = line.indexOf('+', dateStop) + 1;
            final String time = line.substring(offsetStart, offsetStart + 2);
            timeOffset = -1 * Integer.parseInt(time);
        } else if (line.substring(dateStop).contains("-")) {
            final int offsetStart = line.indexOf('-', dateStop) + 1;
            final String time = line.substring(offsetStart, offsetStart + 2);
            timeOffset = Integer.parseInt(time);
        }
        try {
            final Date dateParsed = dateFormat.parse(date);
            return dateParsed.getTime() + timeOffset * MILLIS_IN_HOUR;
        } catch (Exception e) {
            LOGGER.error(e);
        }


        return -1;
    }

    /**
     * @param line
     * @return
     */
    private String extractNodeUrn(final String line) {
        final int nodeUrnStart = line.indexOf(URNTEXT) + URNTEXT.length();
        final int nodeUrnStop = line.indexOf(ENDTEXT, nodeUrnStart);
        return line.substring(nodeUrnStart, nodeUrnStop);
    }
}
