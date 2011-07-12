package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 12.07.11
 * Time: 14:33
 */
public interface VisibleElementActionsChangedListener extends EventListener {

    void actionsChanged(VisibleElementActionsChangedEvent e);

}
