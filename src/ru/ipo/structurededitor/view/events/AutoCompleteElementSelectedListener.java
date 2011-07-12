package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 13.07.11
 * Time: 0:14
 */
public interface AutoCompleteElementSelectedListener extends EventListener {

    void elementChanged(AutoCompleteElementSelectedEvent e);

}
