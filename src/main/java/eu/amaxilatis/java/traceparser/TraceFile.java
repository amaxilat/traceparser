package eu.amaxilatis.java.traceparser;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * TraceFile.
 */
public class TraceFile {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TraceFile.class);

    /**
     * traceFile filename.
     */
    private final transient String filename;
    /**
     * traceFile duration.
     */
    private final transient long duration;
    /**
     * traceFile start time.
     */
    private final transient long startTime;

    /**
     * traceFile end time.
     */
    private final transient long endTime;
    /**
     * nodes of the traceFile.
     */
    private final transient List nodeNames = new ArrayList();
    /**
     * total count of lines in traceFile.
     */
    private final transient long lines;

    /**
     * @return
     */
    public final long getEndTime() {
        return endTime;
    }

    /**
     * @return
     */
    public final List<String> getNodeNames() {
        Collections.sort(nodeNames);
        return nodeNames;
    }

    /**
     * @param file
     * @param inputStream
     * @throws IOException
     */
    public TraceFile(final String file, final InputStream inputStream) throws IOException {


        long countStartTime = 0;
        long countEndTime = 0;
        nodeNames.clear();

        filename = file;

        long max = 0, min = 0;
        long countLines = 0;


        InputStreamReader inputStreamReader = null;
        inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(inputStreamReader);

        String strLine;
        long tempDuration = 0;
        //Read File Line By Line
        while ((strLine = bufferedReader.readLine()) != null) {
            countLines++;
            // Print the content on the console
            final TraceMessage message = new TraceMessage(strLine);
            final long date = message.getTime();
            if (date < countStartTime) {
                min = countLines;
                countStartTime = date;
                tempDuration = countEndTime - countStartTime;
            } else if (date > countEndTime) {
                max = countLines;
                countEndTime = date;
                tempDuration = countEndTime - countStartTime;
            }

            final String nodeUrn = message.getUrn();
            if (!nodeNames.contains(nodeUrn)) {
                nodeNames.add(nodeUrn);
            }

        }
        duration = tempDuration;
        lines = countLines;
        startTime = countStartTime;
        endTime = countEndTime;


        LOGGER.info("Date Started(" + min + ") : " + new Date(startTime));
        LOGGER.info("Date Ended(" + max + ") : " + new Date(endTime));
    }

    /**
     * @return
     */
    public final long getDuration() {
        return duration;
    }

    /**
     * @return
     */
    public final String getFilename() {
        return filename;
    }

    /**
     * @return
     */
    public final long getStartTime() {
        return startTime;
    }

    /**
     * @return
     */
    public final int getNodeSize() {
        return nodeNames.size();
    }

    /**
     * @return
     */
    public final long getLines() {
        return lines;
    }

}
