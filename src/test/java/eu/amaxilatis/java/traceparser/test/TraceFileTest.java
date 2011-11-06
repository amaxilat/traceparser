package eu.amaxilatis.java.traceparser.test;


import eu.amaxilatis.java.traceparser.TraceFile;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.net.URL;


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
        BasicConfigurator.configure();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("src/test/resources/trace.test"));
            TraceFile tracefile = new TraceFile("trace.test", inputStream);
            System.out.println("TraceFile Filename is :" + tracefile.getFilename());
            System.out.println("TraceFile Lines is :" + tracefile.getLines());
            System.out.println("TraceFile NodeSize is :" + tracefile.getNodeSize());
            System.out.println("TraceFile Duration is :" + tracefile.getDuration());
            System.out.println("TraceFile StartTime is :" + tracefile.getStartTime());
            System.out.println("TraceFile EndTime is :" + tracefile.getEndTime());

            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }
}
