package ru.ipo.structurededitor.view.autocomplete;

import ru.ipo.structurededitor.view.TwoSidedCellRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 11.07.11
 * Time: 1:06
 */
public class AutoCompleteCellRenderer extends TwoSidedCellRenderer {

    private static final Icon COMPLETION_ICON = new ImageIcon(AutoCompleteCellRenderer.class.getResource("icons/next.png"));

    private AutoCompleteListComponent listComponent;

    public AutoCompleteCellRenderer(AutoCompleteListComponent listComponent) {
        this.listComponent = listComponent;
        this.errorTextColor = Color.red;
        this.errorTextStyle = Font.PLAIN;
    }

    @Override
    protected void setupRenderData(Object value, int index) {
        if (value == null) {
            renderStyle = RenderStyle.NothingFound;

            searchString = listComponent.getSearchString();

            if (searchString == null)
                searchString = "";

            if (searchString.length() > 10)
                searchString = searchString.substring(0, 10) + "…";

            leftText = "Не найдено: \"" + searchString + "\"";

            icon = null;
        } else {
            renderStyle = RenderStyle.ShortcutAndDescription;

            AutoCompleteElement element = (AutoCompleteElement) value;

            leftText = element.getShortcut();
            rightText = element.getDescription();

            searchString = listComponent.getSearchString();

            icon = COMPLETION_ICON;
        }
    }

}
