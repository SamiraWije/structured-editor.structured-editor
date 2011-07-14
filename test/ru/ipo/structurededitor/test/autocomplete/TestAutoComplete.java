package ru.ipo.structurededitor.test.autocomplete;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 2:31
 */
public class TestAutoComplete {

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
        StructuredEditor.initializeStructuredEditorUI();

        StructuredEditorModel model = new StructuredEditorModel(new RootBean());

        DSLBeansRegistry registry = new DSLBeansRegistry();

        registry.registerBean(RootBean.class);
        registry.registerBean(AbstractBean.class);
        registry.registerBean(AbstractExtensionBean.class);
        registry.registerBean(ExtensionBean1.class);
        registry.registerBean(ExtensionBean2.class);
        registry.registerBean(ExtensionBean3.class);
        registry.registerBean(ExtensionBean4.class);

        model.setBeansRegistry(registry);

        StructuredEditor editor = new StructuredEditor(model);

        JFrame frame = new JFrame("Editor test");
        frame.setLayout(new BorderLayout());
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);

        frame.add(editor, BorderLayout.CENTER);
        frame.add(new JScrollPane(editor.getActionsListComponent()), BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
