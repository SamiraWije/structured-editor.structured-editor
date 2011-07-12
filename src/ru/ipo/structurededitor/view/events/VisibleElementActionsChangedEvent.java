package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 12.07.11
 * Time: 14:33
 */
public class VisibleElementActionsChangedEvent extends EventObject {

    private final VisibleElement element;

    public VisibleElementActionsChangedEvent(Object source, VisibleElement element) {
        super(source);
        this.element = element;
    }

    public VisibleElement getElement() {
        return element;
    }
}
