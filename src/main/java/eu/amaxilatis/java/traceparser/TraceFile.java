package eu.amaxilatis.java.traceparser;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class TraceFile {

    private final static Logger LOGGER = Logger.getLogger(TraceFile.class);

    private final String filename;
    private long duration;
    private long startTime;

    public long getEndTime() {
        return endTime;
    }

    private long endTime;

    public List<String> getNodeNames() {
        Collections.sort(nodeNames);
        return nodeNames;
    }

    private final List nodeNames = new ArrayList();
    private long lines;


    public TraceFile(final String file, final InputStream inputStream) throws IOException {


        startTime = (new Date()).getTime();
        endTime = 0;
        lines = 0;
        nodeNames.clear();

        filename = file;

        long max = 0, min = 0;

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

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


        LOGGER.info("Date Started(" + min + ") : " + new Date(startTime).toString());
        LOGGER.info("Date Ended(" + max + ") : " + new Date(endTime).toString());
    }


    public long getDuration() {
        return duration;
    }

    public String getFilename() {
        return filename;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getNodeSize() {
        return nodeNames.size();
    }

    public long getLines() {
        return lines;
    }

}
