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
    private final String filename;
    /**
     * traceFile duration.
     */
    private long duration;
    /**
     * traceFile start time.
     */
    private long startTime;

    /**
     * traceFile end time.
     */
    private long endTime;
    /**
     * nodes of the traceFile.
     */
    private final List nodeNames = new ArrayList();
    /**
     * total count of lines in traceFile.
     */
    private long lines;

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


        startTime = (new Date()).getTime();
        endTime = 0;
        lines = 0;
        nodeNames.clear();

        filename = file;

        long max = 0, min = 0;

        InputStreamReader inputStreamReader = null;
        inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(inputStreamReader);

        String strLine;
        long tempDuration = 0;
        //Read File Line By Line
        while ((strLine = bufferedReader.readLine()) != null) {
            lines++;
            // Print the content on the console
            final TraceMessage m = new TraceMessage(strLine);
            final long date = m.getTime();
            if (date < startTime) {
                min = lines;
                startTime = date;
                tempDuration = endTime - startTime;
            } else if (date > endTime) {
                max = lines;
                endTime = date;
                tempDuration = endTime - startTime;
            }

            final String nodeurn = m.getUrn();
            if (!nodeNames.contains(nodeurn)) {
                nodeNames.add(nodeurn);
            }

        }
        duration = tempDuration;


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
