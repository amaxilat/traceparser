package eu.amaxilatis.java.traceparser.traces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/10/11
 * Time: 5:09 PM
 */
public class RuntimeTraceMessage extends AbstractTraceMessage {
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeTraceMessage.class);
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
     *
     */
    private static final int MILLIS_IN_HOUR = 60 * 60 * 1000;
    /**
     *
     */
    private transient String strLine = "";


    /**
     *
     */
    public RuntimeTraceMessage() {
        //empty
    }

    /**
     * @param strLine the string message
     */
    public void setString(final String strLine) {
        this.strLine = strLine;
    }

    public String getStrLine() {
        return strLine;
    }

    /**
     * @return
     */
    public String getText() {
        final int textStart = strLine.indexOf(TEXTTEXT) + TEXTTEXT.length();
        final int textStop = strLine.indexOf(ENDTEXT, textStart);
        return strLine.substring(textStart, textStop);
    }

    /**
     * @return
     */
    public String getLevel() {
        final int levelStart = strLine.indexOf(LEVELTEXT) + LEVELTEXT.length();
        final int levelStop = strLine.indexOf(ENDTEXT, levelStart);
        return strLine.substring(levelStart, levelStop);
    }

    /**
     * @return
     */
    public long getTime() {
        final int dateStart = strLine.indexOf(DATETEXT) + DATETEXT.length();

        //final int dateStop = strLine.indexOf("+", dateStart);
        final int dateStop = dateStart + 23;
        final String date = strLine.substring(dateStart, dateStop);
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S", Locale.US);
        int timeOffset = 0;
        if (strLine.substring(dateStop).contains("+")) {
            final int offsetStart = strLine.indexOf('+', dateStop) + 1;
            final String time = strLine.substring(offsetStart, offsetStart + 2);
            timeOffset = -1 * Integer.parseInt(time);
        } else if (strLine.substring(dateStop).contains("-")) {
            final int offsetStart = strLine.indexOf('-', dateStop) + 1;
            final String time = strLine.substring(offsetStart, offsetStart + 2);
            timeOffset = Integer.parseInt(time);
        }
        try {
            final Date dateParsed = dateFormat.parse(date);
            return dateParsed.getTime() + timeOffset * MILLIS_IN_HOUR;
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }


        return -1;
    }

    /**
     * @return
     */
    public String getUrn() {
        final int nodeUrnStart = strLine.indexOf(URNTEXT) + URNTEXT.length();
        final int nodeUrnStop = strLine.indexOf(ENDTEXT, nodeUrnStart);
        return strLine.substring(nodeUrnStart, nodeUrnStop);
    }
}
