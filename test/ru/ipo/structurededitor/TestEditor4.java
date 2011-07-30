package ru.ipo.structurededitor;

import ru.ipo.structurededitor.controller.ModificationHistory;
import ru.ipo.structurededitor.controller.ModificationListener;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.testLang.testLang.*;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 17:05:46
 */
public class TestEditor4 {

    private final JButton undo = new JButton("undo");
    private final JButton redo = new JButton("redo");
    private StructuredEditor structuredEditor;

    public static void main(String[] args) {
        Runnable go = new Runnable() {
            public void run() {
                new TestEditor4();
            }
        };

        SwingUtilities.invokeLater(go);
    }

    public TestEditor4() {
        JFrame f = new JFrame("Test Editor");

        Bean2 bean2 = new Bean2();
        final StructuredEditorModel model = createModel(bean2);
        structuredEditor = new StructuredEditor(model);

        f.setLayout(new BorderLayout());

        f.add(new StructuredEditorWithActions(structuredEditor), BorderLayout.CENTER);
        f.add(createButtonsPanel(), BorderLayout.NORTH);

        updateUndoRedoEnabled();

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(640, 480);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(undo);
        panel.add(redo);

        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                structuredEditor.undo();
            }
        });

        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                structuredEditor.redo();
            }
        });

        structuredEditor.addModificationListener(new ModificationListener() {
            @Override
            public void modificationPerformed() {
                updateUndoRedoEnabled();
            }
        });

        return panel;
    }

    private StructuredEditorModel createModel(DSLBean bean2) {
        StructuredEditor.initializeStructuredEditorUI();

        DSLBeansRegistry reg = new DSLBeansRegistry();

        reg.registerBean(Bean1.class);
        reg.registerBean(Bean2.class);
        reg.registerBean(BeanA.class);
        reg.registerBean(BeanA1.class);
        reg.registerBean(BeanA2.class);
        reg.registerBean(BeanA3.class);

        StructuredEditorModel model = new StructuredEditorModel(bean2);
        model.setBeansRegistry(reg);
        return model;
    }

    private void updateUndoRedoEnabled() {
        redo.setEnabled(structuredEditor.canRedo());
        undo.setEnabled(structuredEditor.canUndo());
    }
}
