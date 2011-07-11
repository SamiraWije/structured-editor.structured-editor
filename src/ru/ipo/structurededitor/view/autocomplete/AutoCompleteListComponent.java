package ru.ipo.structurededitor.view.autocomplete;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 10.07.11
 * Time: 0:49
 */
public class AutoCompleteListComponent extends JScrollPane {

    private static final ListCellRenderer cellRenderer = new AutoCompleteCellRenderer();
//    private static final int MAX_HEIGHT = 300;

    public static JComponent getComponent(AutoCompleteElement... elementsToSelect) {
        return getComponent(Arrays.asList(elementsToSelect), null);
    }

    public static AutoCompleteListComponent getComponent(Collection<AutoCompleteElement> elementsToSelect, String searchString) {
        AutoCompleteListComponent component = new AutoCompleteListComponent(elementsToSelect);
        component.setSearchString(searchString);
        return component;
    }

    private AutoCompleteListComponent(Collection<AutoCompleteElement> elementsToSelect) {
        super(createList(elementsToSelect));
        setFocusable(false);
    }

    private static JComponent createList(Collection<AutoCompleteElement> elementsToSelect) {
        JList list = new JList(new AutoCompleteListModel(elementsToSelect));
        list.setCellRenderer(cellRenderer);
        list.setFocusable(false);
        return list;
    }

    private JList getList() {
        return (JList) getViewport().getView();
    }

    private AutoCompleteListModel getModel() {
        return (AutoCompleteListModel) getList().getModel();
    }

    public AutoCompleteElement getSelectedElement() {
        return (AutoCompleteElement) getList().getSelectedValue();
    }

    public void moveSelection(int amount, boolean allowCycle) {
        JList list = getList();
        int selectedIndex = list.getSelectedIndex();

        if (selectedIndex < 0)
            if (amount > 0)
                selectedIndex = -1;
            else
                selectedIndex = 0;

        selectedIndex += amount;

        int total = list.getModel().getSize();

        if (total == 0)
            return;

        if (allowCycle) {
            while (selectedIndex >= total)
                selectedIndex -= total;
            while (selectedIndex < 0)
                selectedIndex += total;
        } else {
            if (selectedIndex >= total)
                selectedIndex = total - 1;
            if (selectedIndex < 0)
                selectedIndex = 0;
        }

        list.setSelectedIndex(selectedIndex);
    }

    public void setSearchString(String searchString) {
        AutoCompleteListModel model = getModel();
        model.setSearchString(searchString);

        //set size of the component
        JList list = getList();
        int modelSize = model.getSize();
        list.setPrototypeCellValue(model.getTheLongestElement());
        list.setVisibleRowCount(modelSize > 10 ? 10 : modelSize);
    }

    public String getSearchString() {
        return getModel().getSearchString();
    }
}
