package eu.amaxilatis.java.traceparser;

import eu.amaxilatis.java.traceparser.frames.TraceParserFrame;

/**
 *
 */
public class TraceParserApp {
    /**
     * instance of singletron
     */
    private static TraceParserApp instance = new TraceParserApp();

    /**
     * @return
     */
    public static TraceParserApp getInstance() {
        return instance;
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        new TraceParserFrame();
    }
}
