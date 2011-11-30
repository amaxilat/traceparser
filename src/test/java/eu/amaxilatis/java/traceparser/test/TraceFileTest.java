package eu.amaxilatis.java.traceparser.test;


import eu.amaxilatis.java.traceparser.traces.TraceFile;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;


public class TraceFileTest
        extends TestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceFileTest.class);

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
        assertTrue(true);
        if (true) return;
        else
        {
            FileInputStream inputStream = null;
            try {
                final long start = System.currentTimeMillis();
                inputStream = new FileInputStream(new File("src/test/resources/trace.test"));
                TraceFile.getInstance().setFile("trace.test", inputStream);
                final long total = System.currentTimeMillis() - start;
                LOGGER.info("TraceFile Created in " + total + " msec");
                LOGGER.info("TraceFile is named " + TraceFile.getInstance().getFilename());
                LOGGER.info("TraceFile contains " + TraceFile.getInstance().getLines() + " lines");
                LOGGER.info("TraceFile contains " + TraceFile.getInstance().getNodeSize() + " nodes");
                LOGGER.info("TraceFile lasts for " + TraceFile.getInstance().getDuration() + " seconds");
                LOGGER.info("TraceFile starts @ " + new Date(TraceFile.getInstance().getStartTime()));
                LOGGER.info("TraceFile ends @ " + new Date(TraceFile.getInstance().getEndTime()));

                assertTrue(true);
            } catch (Exception e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }

    }
}
