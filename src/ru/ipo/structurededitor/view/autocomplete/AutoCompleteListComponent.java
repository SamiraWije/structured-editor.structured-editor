package ru.ipo.structurededitor.view.autocomplete;

import ru.ipo.structurededitor.view.events.AutoCompleteElementSelectedEvent;
import ru.ipo.structurededitor.view.events.AutoCompleteElementSelectedListener;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 10.07.11
 * Time: 0:49
 */
public class AutoCompleteListComponent extends JScrollPane {

    public static final int VISIBLE_ELEMENTS_COUNT = 10;

    private final ListCellRenderer cellRenderer = new AutoCompleteCellRenderer(this);

    public static JComponent getComponent(AutoCompleteElement... elementsToSelect) {
        return getComponent(Arrays.asList(elementsToSelect), null);
    }

    public static AutoCompleteListComponent getComponent(List<AutoCompleteElement> elementsToSelect, String searchString) {
        AutoCompleteListComponent component = new AutoCompleteListComponent(elementsToSelect);
        component.setSearchString(searchString);
        return component;
    }

    private AutoCompleteListComponent(List<AutoCompleteElement> elementsToSelect) {
        super();
        setViewportView(createList(elementsToSelect));
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setFocusable(false);
    }

    private JComponent createList(List<AutoCompleteElement> elementsToSelect) {
        final JList list = new JList(new AutoCompleteListModel(elementsToSelect));
        list.setCellRenderer(cellRenderer);
        list.setFocusable(false);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    Object selectedValue = list.getSelectedValue();
                    if (selectedValue != null)
                        fireAutoCompleteElementSelectedListener((AutoCompleteElement) selectedValue);
                }
            }
        });
        return list;
    }

    private JList getList() {
        return (JList) getViewport().getView();
    }

    public AutoCompleteListModel getModel() {
        return (AutoCompleteListModel) getList().getModel();
    }

    public AutoCompleteElement getSelectedElement() {
        return (AutoCompleteElement) getList().getSelectedValue();
    }

    public AutoCompleteElement getElementByShortcut(String shortcut) {
        return getModel().getElementByShortcut(shortcut);
    }

    //TODO don't select first element if it is empty
    public void moveSelection(int amount, boolean allowCycle) {
        JList list = getList();

        int selectedIndex = list.getSelectedIndex();
        int total = list.getModel().getSize();

        if (selectedIndex < 0)
            if (amount > 0)
                selectedIndex = -1;
            else
                selectedIndex = 0;

        selectedIndex += amount;

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
        list.ensureIndexIsVisible(selectedIndex);
    }

    public void setSearchString(String searchString) {
        AutoCompleteListModel model = getModel();
        model.setSearchString(searchString);

        //set size of the component
        JList list = getList();
        int modelSize = model.getSize();
        list.setPrototypeCellValue(model.getTheLongestElement());
        list.setFixedCellWidth(-1);
        list.setVisibleRowCount(modelSize > VISIBLE_ELEMENTS_COUNT ? VISIBLE_ELEMENTS_COUNT : modelSize);
    }

    public String getSearchString() {
        return getModel().getSearchString();
    }

    public int getFilteredElementsCount() {
        return getModel().getFilteredElementsCount();
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        getList().addListSelectionListener(listener);
    }

    public void removeListSelectionListener(ListSelectionListener listener) {
        getList().removeListSelectionListener(listener);
    }

    public void addAutoCompleteElementSelectedListener(AutoCompleteElementSelectedListener listener) {
        listenerList.add(AutoCompleteElementSelectedListener.class, listener);
    }

    public void removeAutoCompleteElementSelectedListener(AutoCompleteElementSelectedListener listener) {
        listenerList.remove(AutoCompleteElementSelectedListener.class, listener);
    }

    public void fireAutoCompleteElementSelectedListener(AutoCompleteElement element) {
        AutoCompleteElementSelectedEvent e = new AutoCompleteElementSelectedEvent(this, element);

        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == AutoCompleteElementSelectedListener.class)
                ((AutoCompleteElementSelectedListener)listeners[i + 1]).elementChanged(e);
    }

    public AutoCompleteElement getElementAt(int index) {
        return (AutoCompleteElement) getModel().getElementAt(index);
    }
}
