package ru.ipo.structurededitor.actions;

import ru.ipo.structurededitor.view.DataShowUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 11.07.11
 * Time: 1:06
 */
public class ActionsCellRenderer extends DefaultListCellRenderer {

    private ActionsListComponent actionsListComponent;

    public ActionsCellRenderer(ActionsListComponent actionsListComponent) {
        this.actionsListComponent = actionsListComponent;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        VisibleElementAction action = (VisibleElementAction) value;

        if (action != null) {
            String strokeDescription;
            if (actionsListComponent.hasKeyStroke(action))
                strokeDescription = "(" + DataShowUtils.keyStroke2String(action.getKeyStroke()) + ")";
            else
                strokeDescription = "";

            value = DataShowUtils.htmlLayoutDataAndHint(action.getActionText(), strokeDescription);
        } else {
            value = DataShowUtils.htmlLayoutNothing("(Нет доступных действий)");
        }

        JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, index == ((ActionsListComponent) list).getHighlightIndex(), false);

        if (action != null)
            renderer.setIcon(action.getIcon());

        return renderer;
    }
}
