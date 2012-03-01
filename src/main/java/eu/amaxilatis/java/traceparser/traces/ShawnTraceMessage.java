package eu.amaxilatis.java.traceparser.traces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/10/11
 * Time: 5:09 PM
 */
public class ShawnTraceMessage extends AbstractTraceMessage {
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
    private static final char ENDTEXT = ']';

    /**
     *
     */
    private transient String strLine = "";


    /**
     *
     */
    public ShawnTraceMessage() {
        LOGGER.trace("new ShawnTraceMessage");
        //empty
    }

    /**
     * @param strLine the string message
     */
    public void setString(final String strLine) {
        this.strLine = strLine;
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

        final int dateStop = strLine.lastIndexOf(ENDTEXT);
        return Long.parseLong(strLine.substring(dateStart, dateStop) + "000");

    }

    /**
     * @return
     */
    public String getUrn() {
        final int nodeUrnStart = strLine.indexOf(URNTEXT) + URNTEXT.length();
        final int nodeUrnStop = strLine.indexOf(ENDTEXT, nodeUrnStart);
        return strLine.substring(nodeUrnStart, nodeUrnStop);
    }

    @Override
    public String getStrLine() {
        return strLine;
    }
}
