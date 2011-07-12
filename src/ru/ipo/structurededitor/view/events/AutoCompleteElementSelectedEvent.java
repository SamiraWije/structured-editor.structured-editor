package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 13.07.11
 * Time: 0:14
 */
public class AutoCompleteElementSelectedEvent extends EventObject {

    private AutoCompleteElement element;

    public AutoCompleteElementSelectedEvent(Object source, AutoCompleteElement element) {
        super(source);
        this.element = element;
    }

    public AutoCompleteElement getElement() {
        return element;
    }
}
