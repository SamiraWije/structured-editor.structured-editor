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
//    private static final String spacing = "  ";
//    private static final String ending = "…";
//    private static final int maxWidth = 42;

    private static final Icon COMPLETION_ICON = new ImageIcon(AutoCompleteCellRenderer.class.getResource("icons/next.png"));

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (list == null)
            return null;

        if (value == AutoCompleteListModel.EMPTY_LIST_OBJECT)
            value = DataShowUtils.htmlLayout("", "(Ничего не найдено)");
        else {
            AutoCompleteElement element = (AutoCompleteElement) value;

            value = DataShowUtils.htmlLayout(
                    element.getShortcut(),
                    element.getDescription()
            );
        }

        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        ((JLabel) component).setIcon(COMPLETION_ICON);

        return component;
    }
}
