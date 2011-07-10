package eu.amaxilatis.java.traceparser;


// Import log4j classes.

// Import log4j classes.

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * Hello world!
 */
public class TraceParserApp {

    public static Logger log = Logger.getLogger(TraceParserApp.class);

    public static void main(String[] args) {
        TraceParserFrame appframe = new TraceParserFrame();

        BasicConfigurator.configure();
        log.info("App stared!");

    }
}
