package eu.amaxilatis.java.traceparser.panels;

import eu.amaxilatis.java.traceparser.parsers.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 10/10/11
 * Time: 7:29 PM
 */
public class ParserControlPanel extends JPanel implements ActionListener {

    private static final Logger log = Logger.getLogger(ParserControlPanel.class);

    private List<AbstractParser> parserList;
    private Properties properties;
    private final javax.swing.JTextField[] parserOptionsText = new javax.swing.JTextField[3];

    public ParserControlPanel(Properties properties) {
        this.properties = properties;
        parserList = new ArrayList<AbstractParser>();
        this.setLayout(new java.awt.GridLayout(3, 2, 10, 10));

        initComponents();
    }

    private void initComponents() {

        final String[] parserOptionsTexts = properties.getProperty("parser.templates").split(",");
        final String[] parserOptionsLabels = {"Send Text", "Cluster Text", "Event Text"};
        for (int i = 0; i < 3; i++) {
            parserOptionsText[i] = new javax.swing.JTextField(parserOptionsTexts[i]);
            this.add(new javax.swing.JLabel(parserOptionsLabels[i]));
            this.add(parserOptionsText[i]);
        }
        parserList.add(new NeighborhoodParser());
        parserList.add(new SendParser(parserOptionsText[0].getText()));
        parserList.add(new ClustersParser(parserOptionsText[1].getText()));
        parserList.add(new EventParser(parserOptionsText[2].getText()));
    }

    public List<AbstractParser> getParsers() {
        return parserList;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        final Object e = actionEvent.getSource();
        if (e.equals("")) {

        } else {

            log.debug(e.toString());
        }
    }


}
