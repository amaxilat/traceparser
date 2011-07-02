package eu.amaxilatis.java.traceparser;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 6/23/11
 * Time: 12:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class logger {
    public static int DEBUG=0;
    public static int INFO=1;
    public static int EXTRA=2;
    public static int level=0;

    public logger() {
        level=0;
    }

    public void setLevel(int new_level){
                            level=new_level;
    }

    public void debug(String s) {
        System.out.println("Debug:" + s);
    }
    public void debug(String s, int dlevel){
        if (dlevel<=level){
            System.out.println("Debug:" + s);
        }
    }
}
