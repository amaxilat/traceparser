package eu.amaxilatis.java.traceparser;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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


    public TraceFile(final String file) {

        duration = 0;
        startTime = (new Date()).getTime();
        endTime = 0;
        lines = 0;
        nodeNames.clear();

        filename = file;

        long max = 0, min = 0;
        try {
            // Open the file that is the first
            // command line parameter
            final FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            final DataInputStream in = new DataInputStream(fstream);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = bufferedReader.readLine()) != null) {
                lines++;
                // Print the content on the console
                final TraceMessage m = new TraceMessage(strLine);
                final long date = m.getTime();
                if (date < startTime) {
                    min = lines;
                    startTime = date;
                    duration = endTime - startTime;
                } else if (date > endTime) {
                    max = lines;
                    endTime = date;
                    duration = endTime - startTime;
                }

                final String nodeurn = m.getUrn();
                if (!nodeNames.contains(nodeurn)) {
                    nodeNames.add(nodeurn);
                }

            }


            LOGGER.info("Date Started(" + min + ") : " + new Date(starttime()).toString());
            LOGGER.info("Date Ended(" + max + ") : " + new Date(endtime()).toString());
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            LOGGER.error("Error: reading the file : " + filename + " line " + lines);
            LOGGER.error(e);
        }


    }


    public long getDuration() {
        return duration;
    }

    public String getFilename() {
        return filename;
    }

    public long starttime() {
        return startTime;
    }

    long endtime() {
        return endTime;
    }

    public int nodesize() {
        return nodeNames.size();
    }

    public long getLines() {
        return lines;
    }

}
