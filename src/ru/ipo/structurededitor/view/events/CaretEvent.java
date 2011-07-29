package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.Display;

import java.util.EventObject;

/**
 * Event: caret is need to be output
 */
public class CaretEvent extends EventObject {

    public CaretEvent(Object source) {
        super(source);
    }

}
