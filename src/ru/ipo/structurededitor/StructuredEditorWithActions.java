package ru.ipo.structurededitor;

import ru.ipo.structurededitor.actions.ActionsListComponent;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 29.07.11
 * Time: 13:17
 */
public class StructuredEditorWithActions extends JPanel {

    private final StructuredEditor structuredEditor;
    private final ActionsListComponent actionsComponent;

    public StructuredEditorWithActions(StructuredEditor structuredEditor) {
        this.structuredEditor = structuredEditor;
        this.actionsComponent = structuredEditor.getActionsListComponent();

        setLayout(new BorderLayout());

        add(new JScrollPane(structuredEditor), BorderLayout.CENTER);
        add(new JScrollPane(actionsComponent), BorderLayout.SOUTH);
    }

    public StructuredEditor getStructuredEditor() {
        return structuredEditor;
    }

    public ActionsListComponent getActionsComponent() {
        return actionsComponent;
    }
}
