package ru.ipo.structurededitor.view.autocomplete;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 10.07.11
 * Time: 0:39
 */
public interface AutoCompletionSelectedListener extends EventListener {

    void completionSelected(AutoCompletionSelectedEvent e);

}
