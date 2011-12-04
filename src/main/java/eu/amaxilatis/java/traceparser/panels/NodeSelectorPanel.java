package eu.amaxilatis.java.traceparser.panels;

import eu.amaxilatis.java.traceparser.traces.TraceFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * A panel that allow user to select which nodes will be used during parsing.
 */
public class NodeSelectorPanel extends JPanel implements ActionListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeSelectorPanel.class);

    /**
     * Panel name.
     */
    public static final String NAME = "Node Selection";

    /**
     * Update button name.
     */
    private static final JButton UPDATE_BUTTON = new JButton("Reload Selections");

    /**
     * counter label.
     */
    private static final JLabel ENABLED_COUNT = new JLabel("unset");

    /**
     * List of nodes.
     */
    private static JList nodesList = new JList(new DefaultListModel());
    /**
     * Selected nodes map.
     */
    private static Map<String, Integer> selectNodesMap = new HashMap<String, Integer>();

    /**
     * Constructor.
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
     * even listener.
     *
     * @param actionEvent event happened
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(UPDATE_BUTTON)) {
            updateSelected();
        }

    }

    /**
     * update button pushed.
     */
    private static void updateSelected() {
        ENABLED_COUNT.setText(String.valueOf(nodesList.getSelectedIndices().length));
        selectNodesMap = new HashMap<String, Integer>();
        for (Object selectedNode : nodesList.getSelectedValues()) {
            selectNodesMap.put((String) selectedNode, 1);
        }
    }

    /**
     * update action.
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
     * checks if a node is selected.
     *
     * @param urn the node urn
     * @return true if selected
     */
    public static boolean isSelected(final String urn) {
        return selectNodesMap.containsKey(urn);
    }
}
