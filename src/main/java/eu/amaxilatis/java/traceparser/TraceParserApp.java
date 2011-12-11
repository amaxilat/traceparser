package eu.amaxilatis.java.traceparser;

import eu.amaxilatis.java.traceparser.frames.TraceParserFrame;

import java.io.IOException;
import java.util.Properties;

/**
 * TraceParserApp
 */
public class TraceParserApp {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        String title = "";
        try {
            final Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties"));
            StringBuilder builder = new StringBuilder();
            builder.append(properties.getProperty("name"));
            builder.append(" Version:").append(properties.getProperty("version"));
            builder.append(" Build:").append(properties.getProperty("build"));
            title = builder.toString();

        } catch (IOException e) {

        }
        new TraceParserFrame(title);
    }


}
