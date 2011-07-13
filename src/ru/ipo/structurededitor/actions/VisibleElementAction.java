package ru.ipo.structurededitor.actions;

import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 10.07.11
 * Time: 21:45
 */
public abstract class VisibleElementAction {

    private final String actionText;
    private final Icon icon;
    private final KeyStroke keyStroke;

    public abstract void run(StructuredEditorModel model);

    public VisibleElementAction(String actionText, KeyStroke keyStroke) {
        this(actionText, getIcon("key.png"), keyStroke);
    }

    public VisibleElementAction(String actionText, String iconName, KeyStroke keyStroke) {
        this(actionText, getIcon(iconName), keyStroke);
    }

    public VisibleElementAction(String actionText, Icon icon, KeyStroke keyStroke) {
        this.actionText = actionText;
        this.icon = icon;
        this.keyStroke = keyStroke;
    }

    private static Icon getIcon(String name) {
        return new ImageIcon(
                VisibleElementAction.class.getResource("icons/" + name)
        );
    }

    public String getActionText() {
        return actionText;
    }

    public Icon getIcon() {
        return icon;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}