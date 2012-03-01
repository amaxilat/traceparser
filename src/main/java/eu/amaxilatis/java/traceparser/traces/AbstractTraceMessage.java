package eu.amaxilatis.java.traceparser.traces;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/10/11
 * Time: 5:09 PM
 */
public abstract class AbstractTraceMessage {

    /**
     * @param strLine the string message
     */
    public abstract void setString(final String strLine);


    /**
     * @return
     */
    public abstract String getText();

    /**
     * @return
     */
    public abstract String getLevel();

    /**
     * @return
     */
    public abstract long getTime();

    /**
     * @return
     */
    public abstract String getUrn();

    public abstract String getStrLine();
}
