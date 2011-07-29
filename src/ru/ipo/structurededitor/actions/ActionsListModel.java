package ru.ipo.structurededitor.actions;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 29.07.11
 * Time: 13:36
 */
public class ActionsListModel extends AbstractListModel {

    private ArrayList<VisibleElementAction> actions = new ArrayList<VisibleElementAction>();

    @Override
    public int getSize() {
        return actions.size() == 0 ? 1 : actions.size();
    }

    @Override
    public Object getElementAt(int index) {
        return actions.size() == 0 ? null : actions.get(index);
    }

    public void addElement(VisibleElementAction action) {
        if (actions.size() == 0)
            fireIntervalAdded(this, 0, 0);

        actions.add(action);
        fireIntervalAdded(this, actions.size() - 1, actions.size() - 1);
    }

    public void removeElement(VisibleElementAction action) {
        int ind = actions.indexOf(action);
        if (ind < 0)
            return;

        actions.remove(ind);

        fireIntervalRemoved(this, ind, ind);

        if (actions.size() == 0)
            fireIntervalAdded(this, 0, 0);
    }

    public void clear() {
        int size = getSize();
        actions.clear();

        fireIntervalRemoved(this, 0, size - 1);
        fireIntervalAdded(this, 0, 0);
    }
}
