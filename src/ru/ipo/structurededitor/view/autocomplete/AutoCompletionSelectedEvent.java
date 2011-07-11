package ru.ipo.structurededitor.view.autocomplete;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 10.07.11
 * Time: 0:38
 */
public class AutoCompletionSelectedEvent extends EventObject {

    private AutoCompleteElement element;

    public AutoCompletionSelectedEvent(Object source, AutoCompleteElement element) {
        super(source);
        this.element = element;
    }

    public AutoCompleteElement getElement() {
        return element;
    }
}
