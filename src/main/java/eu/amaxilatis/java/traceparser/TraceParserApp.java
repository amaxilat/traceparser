package eu.amaxilatis.java.traceparser;


import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;


public class TraceParserApp {

    public static final Logger LOGGER = Logger.getLogger(TraceParserApp.class);

    public static void main(final String[] args) {
        new TraceParserFrame();

        PropertyConfigurator.configure(TraceParserApp.class.getClassLoader().getResource("log4j.properties"));
        LOGGER.info("App stared!");

    }
}
