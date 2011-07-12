package ru.ipo.structurededitor.view.autocomplete;

import ru.ipo.structurededitor.view.DataShowUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 07.07.11
 * Time: 23:51
 */
public class AutoCompleteCellRenderer extends DefaultListCellRenderer {
//    private static final String spacing = " ";
//    private static final String ending = "…";
//    private static final int maxWidth = 42;

    private static final Icon COMPLETION_ICON = new ImageIcon(AutoCompleteCellRenderer.class.getResource("icons/next.png"));

    private AutoCompleteListComponent listComponent;
    private static final String highlightColor = "#DD00DD";

    public AutoCompleteCellRenderer(AutoCompleteListComponent listComponent) {
        this.listComponent = listComponent;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (list == null)
            return null;

        boolean needIcon = true;

        if (value == null) {
            String searchString = listComponent.getSearchString();
            if (searchString == null)
                searchString = "";

            //TODO properly escape html
            searchString = searchString
                    .replaceAll("&", "&amp;")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;");

            value = DataShowUtils.htmlLayout("", "Для \"" + searchString + "\" соответствий не найдено");

            needIcon = false;
        } else {
            AutoCompleteElement element = (AutoCompleteElement) value;

            String shortcut = element.getShortcut();
            String description = element.getDescription();

            shortcut = highlightSearchString(shortcut);
            description = highlightSearchString(description);

            value = DataShowUtils.htmlLayout(
                    shortcut,
                    description
            );
        }

        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (needIcon)
            ((JLabel) component).setIcon(COMPLETION_ICON);
        else
            ((JLabel) component).setIcon(null);

        return component;
    }

    private String highlightSearchString(String string) {
        String searchString = listComponent.getModel().getSearchString();

        if (searchString == null)
            return string;

        if (string == null)
            return string;

        searchString = searchString.toLowerCase();

        int i = string.toLowerCase().indexOf(searchString);
        if (i < 0)
            return string;

        int j = i + searchString.length();

        StringBuilder sb = new StringBuilder();
        sb
                .append(string.substring(0, i))

                .append("<span color='")
                .append(highlightColor)
                .append("'>")

                .append(string.substring(i, j))
                .append("</span>")
                .append(string.substring(j));

        return sb.toString();
    }
}
