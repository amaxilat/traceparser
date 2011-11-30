package eu.amaxilatis.java.traceparser;

import eu.amaxilatis.java.traceparser.frames.TraceParserFrame;

/**
 *
 */
public class TraceParserAppSingletron {
    /**
     * instance of singletron
     */
    private static TraceParserAppSingletron instance = new TraceParserAppSingletron();

    /**
     * @return
     */
    public static TraceParserAppSingletron getInstance() {
        return instance;
    }

    /**
     * singletron
     */
    public TraceParserAppSingletron() {

    }


    /**
     * @param args
     */
    public static void main(final String[] args) {
        new TraceParserFrame();
    }
}
