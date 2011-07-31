package ru.ipo.structurededitor.actions;

import ru.ipo.structurededitor.view.DataShowUtils;
import ru.ipo.structurededitor.view.TwoSidedCellRenderer;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 11.07.11
 * Time: 1:06
 */
public class ActionsCellRenderer extends TwoSidedCellRenderer {

    private ActionsListComponent actionsListComponent;

    public ActionsCellRenderer(ActionsListComponent actionsListComponent) {
        this.actionsListComponent = actionsListComponent;
        this.errorTextStyle = Font.ITALIC;
        this.errorTextColor = new Color(0x444444);
        this.rightTextToTheRight = false;
    }

    @Override
    protected void setupRenderData(Object value, int index) {
        VisibleElementAction action = (VisibleElementAction) value;

        if (action != null) {
            String strokeDescription;
            if (actionsListComponent.hasKeyStroke(action))
                strokeDescription = "(" + DataShowUtils.keyStroke2String(action.getKeyStroke()) + ")";
            else
                strokeDescription = "";

            this.leftText = action.getActionText();
            this.rightText = strokeDescription;
            this.icon = action.getIcon();
            this.renderStyle = RenderStyle.ShortcutAndDescription;
            this.isSelected = index == actionsListComponent.getHighlightIndex();
        } else {
            this.leftText = "(Нет доступных действий)";
            this.icon = null;
            this.renderStyle = RenderStyle.NothingFound;
        }
    }
}
