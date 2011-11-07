package eu.amaxilatis.java.traceparser.test;


import eu.amaxilatis.java.traceparser.TraceFile;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.net.URL;
import java.util.Date;


public class TraceFileTest
        extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(TraceFileTest.class);

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TraceFileTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TraceFileTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {

        FileInputStream inputStream = null;
        try {
            final long start = System.currentTimeMillis();
            inputStream = new FileInputStream(new File("src/test/resources/trace.test"));
            TraceFile tracefile = new TraceFile("trace.test", inputStream);
            final long total = System.currentTimeMillis() - start;
            LOGGER.info("TraceFile Created in " + total + " msec");
            LOGGER.info("TraceFile is named " + tracefile.getFilename());
            LOGGER.info("TraceFile contains " + tracefile.getLines() + " lines");
            LOGGER.info("TraceFile contains " + tracefile.getNodeSize() + " nodes");
            LOGGER.info("TraceFile lasts for " + tracefile.getDuration() + " seconds");
            LOGGER.info("TraceFile starts @ " + new Date(tracefile.getStartTime()));
            LOGGER.info("TraceFile ends @ " + new Date(tracefile.getEndTime()));

            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }
}
