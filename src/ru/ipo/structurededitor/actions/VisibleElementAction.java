package ru.ipo.structurededitor.actions;

import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 10.07.11
 * Time: 21:45
 */
public abstract class VisibleElementAction {

    //TODO implement: null text and null icon means invisible action that is not displayed in the list of actions
    private String actionText;
    private Icon icon;
    private KeyStroke keyStroke;

    private static Map<String, Icon> iconName2icon = new HashMap<String, Icon>();
    private static Map<String, KeyStroke> keyStrokeId2keyStroke = new HashMap<String, KeyStroke>();

    public abstract void run(StructuredEditorModel model);

    public VisibleElementAction(String actionText, String keyStrokeId) {
        this(actionText, getIcon("key.png"), keyStrokeId);
    }

    public VisibleElementAction(String actionText, String iconName, String keyStrokeId) {
        this(actionText, getIcon(iconName), keyStrokeId);
    }

    public VisibleElementAction(String actionText, Icon icon, String keyStrokeId) {
        this.actionText = actionText;
        this.icon = icon;
        this.keyStroke = keyStrokeId2keyStroke.get(keyStrokeId);

        if (keyStroke == null) {
            keyStroke = KeyStroke.getKeyStroke(keyStrokeId);
            keyStrokeId2keyStroke.put(keyStrokeId, keyStroke);
        }
    }

    private static Icon getIcon(String name) {
        Icon icon = iconName2icon.get(name);
        if (icon == null) {
            icon = new ImageIcon(VisibleElementAction.class.getResource("icons/" + name));
            iconName2icon.put(name, icon);
        }
        return icon;
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

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setKeyStroke(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
    }
}
