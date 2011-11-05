package eu.amaxilatis.java.traceparser;


import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


public class TraceParserApp {

    public static final Logger LOGGER = Logger.getLogger(TraceParserApp.class);

    public static void main(final String[] args) {
        new TraceParserFrame();

        BasicConfigurator.configure();
        LOGGER.info("App stared!");

    }
}
