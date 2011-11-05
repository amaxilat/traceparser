package eu.amaxilatis.java.traceparser.panels;

import eu.amaxilatis.java.traceparser.TraceFile;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class NodeSelectorPanel extends JPanel implements ActionListener {

    public static String NAME = "Node Selection";
    public static TraceFile file = null;
    private JButton updateButton;
    private JLabel enabledCountLabel;
    private static JList NodesList;

    private static HashMap<String, Integer> selectedNodesHashMap = new HashMap<String, Integer>();

    private static final Logger log = Logger.getLogger(NodeSelectorPanel.class);
    private JScrollPane nodeScrollPane;

    public NodeSelectorPanel() {

        this.setLayout(new BorderLayout());

        JPanel mainpanel = new JPanel(new BorderLayout());
        mainpanel.add(new JLabel(NAME), BorderLayout.NORTH);
        updateButton = new JButton("Reload Selections");
        updateButton.addActionListener(this);

        enabledCountLabel = new JLabel("all");
        mainpanel.add(new couplePanel(updateButton, enabledCountLabel), BorderLayout.SOUTH);

        ListModel nodesListModel = new DefaultListModel();
        NodesList = new JList(nodesListModel);
        nodeScrollPane = new JScrollPane(NodesList);

        mainpanel.add(nodeScrollPane);
        log.info(NAME + " initialized");


        this.add(mainpanel);
    }


    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(updateButton)) {
            enabledCountLabel.setText(String.valueOf(NodesList.getSelectedIndices().length));
            selectedNodesHashMap = new HashMap<String, Integer>();
            for (Object selectedNode : NodesList.getSelectedValues()) {
                selectedNodesHashMap.put((String) selectedNode, 1);
            }
        }

    }

    public static void setFile(TraceFile file) {
        NodeSelectorPanel.file = file;
        DefaultListModel listModel = (DefaultListModel) NodesList.getModel();
        listModel.clear();

        int selected[] = new int[file.getNodeSize()];
        int i = 0;
        for (String node : file.getNodeNames()) {
            listModel.addElement(node);
            selected[i] = i++;
        }
        NodesList.setSelectedIndices(selected);

    }

    public static boolean isSelected(String urn) {
        return selectedNodesHashMap.containsKey(urn);
    }
}
