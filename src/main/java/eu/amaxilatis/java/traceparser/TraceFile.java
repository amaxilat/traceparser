package eu.amaxilatis.java.traceparser;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/9/11
 * Time: 12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class TraceFile {

    private String filename;
    private long duration;
    private long start_time;
    private long end_time;
    private ArrayList<String> node_names = new ArrayList();
    private Logger log;
    private long lines;


    public TraceFile(String file) {
        log = TraceParserApp.log;


        duration = 0;
        start_time = (new Date()).getTime();
        end_time = 0;
        lines=0;
        node_names.clear();

        filename = file;

        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                lines++;
                // Print the content on the console
                final TraceMessage m = new TraceMessage(strLine);
                final long date = m.time();
                if (date < start_time) {
                    start_time = date;
                    duration = end_time - start_time;
                } else if (date > end_time) {
                    end_time = date;
                    duration = end_time - start_time;
                }

                final String nodeurn = m.urn();
                if (!node_names.contains(nodeurn)) {
                    node_names.add(nodeurn);
                }

            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            log.error("Error: reading the file : " + filename);
            e.printStackTrace();
            log.error(e.toString());
        }


    }


    public long duration() {
        return duration;
    }

    public String filename() {
        return filename;
    }

    public long starttime() {
        return start_time;
    }

    public long endtime() {
        return end_time;
    }

    public int nodesize() {
        return node_names.size();
    }

    public long lines(){
        return lines;
    }

}
