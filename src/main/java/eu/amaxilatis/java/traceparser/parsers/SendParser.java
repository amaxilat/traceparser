package eu.amaxilatis.java.traceparser.parsers;

import java.util.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/2/11
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendParser implements AbstractParser {

    public SendParser(){

    }

    public void update() {

    }

    public void update(Observable observable, Object o) {
        System.out.println ("OBSERVEDDD!!!!!");
    }
}
