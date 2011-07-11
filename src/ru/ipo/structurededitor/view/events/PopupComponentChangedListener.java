package ru.ipo.structurededitor.view.events;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 20:50
 */
public interface PopupComponentChangedListener extends EventListener {

    void componentChanged(PopupComponentChangedEvent event);

}
