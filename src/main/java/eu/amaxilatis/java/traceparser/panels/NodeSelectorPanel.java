package eu.amaxilatis.java.traceparser.panels;

import eu.amaxilatis.java.traceparser.TraceFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class NodeSelectorPanel extends JPanel implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeSelectorPanel.class);
    public final static String NAME = "Node Selection";
    private final static JButton UPDATE_BUTTON = new JButton("Reload Selections");
    private final static JLabel ENABLED_COUNT = new JLabel("unset");

    private static JList nodesList = new JList(new DefaultListModel());
    private static Map<String, Integer> selectNodesMap = new HashMap<String, Integer>();

    /**
     *
     */
    public NodeSelectorPanel() {

        this.setLayout(new BorderLayout());

        final JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel(NAME), BorderLayout.NORTH);
        UPDATE_BUTTON.addActionListener(this);

        mainPanel.add(new CouplePanel(UPDATE_BUTTON, ENABLED_COUNT), BorderLayout.SOUTH);

        final JScrollPane nodeScrollPane = new JScrollPane(nodesList);

        mainPanel.add(nodeScrollPane);
        LOGGER.info(NAME + " initialized");


        this.add(mainPanel);
    }

    /**
     * @param actionEvent
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(UPDATE_BUTTON)) {
            updateSelected();
        }

    }

    /**
     *
     */
    private static void updateSelected() {
        ENABLED_COUNT.setText(String.valueOf(nodesList.getSelectedIndices().length));
        selectNodesMap = new HashMap<String, Integer>();
        for (Object selectedNode : nodesList.getSelectedValues()) {
            selectNodesMap.put((String) selectedNode, 1);
        }
    }

    /**
     *
     */
    public static void update() {
        final DefaultListModel listModel = (DefaultListModel) nodesList.getModel();
        listModel.clear();

        int selected[] = new int[TraceFile.getInstance().getNodeSize()];
        int count = 0;
        for (String node : TraceFile.getInstance().getNodeNames()) {
            listModel.addElement(node);
            selected[count] = count++;
        }
        nodesList.setSelectedIndices(selected);

        updateSelected();

    }

    /**
     * @param urn
     * @return
     */
    public static boolean isSelected(final String urn) {
        return selectNodesMap.containsKey(urn);
    }
}
