package eu.amaxilatis.java.traceparser;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;

public class TraceReader extends Observable implements Runnable {

    private static final String URN = "Source [";
    private static final String TEXT = "Text [";
    private static final String DATE = "Time [";
    private static final String levelText = "Level [";
    private static final String END = "]";
    private static final Logger LOGGER = Logger.getLogger(TraceReader.class);

    public TraceReader() {
    }


    public void run() {
        int count = 0;

        String strLine = null;
        try {
            // Open the file that is the first
            // command line parameter
            final FileInputStream stream = new FileInputStream(TraceFile.getInstance().getFilename());
            // Get the object of DataInputStream
            final DataInputStream dataInputStream = new DataInputStream(stream);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
            //Read File Line By Line
            while ((strLine = reader.readLine()) != null) {
                // Print the content on the console
                //LOGGER.debug(strLine);
                //LOGGER.debug(extractNodeUrn(strLine) + "@" + extractDate(strLine) + ":" + extractText(strLine));
                notifyObservers(new TraceMessage(strLine));
                notifyObservers();
                this.setChanged();

                count++;
                //Thread.sleep(100);
            }
            //Close the input stream
            dataInputStream.close();
        } catch (Exception e) {//Catch exception if any
            LOGGER.error("Error: " + e.toString() + " line: " + count + " contents " + strLine);
        }
    }

    String extractNodeUrn(final String line) {
        final int nodeurnStart = line.indexOf(URN) + URN.length();
        final int nodeurnStop = line.indexOf(END, nodeurnStart);
        return line.substring(nodeurnStart, nodeurnStop);
    }

    String extractText(final String line) {
        final int text_start = line.indexOf(TEXT) + TEXT.length();
        final int text_stop = line.indexOf(END, text_start);
        return line.substring(text_start, text_stop);
    }

    long extractDate(final String line) {
        final int date_start = line.indexOf(DATE) + DATE.length();
        final int date_stop = line.indexOf("+02:00" + END, date_start);
        final String date = line.substring(date_start, date_stop);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S", Locale.US);
        try {
            final Date parseDate = dateFormat.parse(date);
            return parseDate.getTime();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return -1;
    }
}
