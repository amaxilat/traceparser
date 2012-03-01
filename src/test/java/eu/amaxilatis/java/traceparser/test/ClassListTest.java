package eu.amaxilatis.java.traceparser.test;


import eu.amaxilatis.java.traceparser.ClassLister;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ClassListTest
        extends TestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassListTest.class);

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ClassListTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ClassListTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {

        List<String> classes = ClassLister.getInstance().getClassNames("eu.amaxilatis.java.traceparser.parsers");
        for (String classname : classes) {
            LOGGER.info(classname);
        }
        assertTrue(true);
    }


}
