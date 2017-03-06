package client;

import chunks.ChunkMap;
import client.states.EditorState;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created with IntelliJ IDEA.
 * User: Joseph
 * Date: 1/30/14
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditorForm extends JFrame {
    private JRadioButton addRadioButton;
    private JRadioButton subtractRadioButton;
    private JRadioButton smoothRadioButton;
    private JRadioButton selectRadioButton;
    private JComboBox voxelTypeComboBox;
    private JSlider brushSizeSlider;
    private JPanel rootPanel;
    private JRadioButton paintRadioButton;
    private JButton saveMapButton;
    private JButton loadMapButton;
    private JLabel brushLevelLabel;
    private JLabel brushDiameterLabel;

    private EditorState parent;

    public EditorForm(EditorState editorParent) {
        super("Editor");

        this.parent = editorParent;

        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setVisible(true);
        pack();

        brushSizeSlider.setMinimum(0);
        brushSizeSlider.setValue(0);
        brushSizeSlider.setMaximum(ChunkMap.OT_ROOT_LEVEL - 1);

        selectRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    parent.setBrushMode(EditorState.BrushMode.Select);
            }
        });
        paintRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    parent.setBrushMode(EditorState.BrushMode.Paint);
            }
        });
        addRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    parent.setBrushMode(EditorState.BrushMode.AddSubtract);
            }
        });
        smoothRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    parent.setBrushMode(EditorState.BrushMode.Smooth);
            }
        });
        brushSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int level = brushSizeSlider.getValue();
                int diameter = (2 << level) / 2;;
                brushLevelLabel.setText(String.format("Level: %s", level));
                brushDiameterLabel.setText(String.format("Diameter: %s", diameter));
                parent.setBrushLevel(level);
            }
        });
    }
}
