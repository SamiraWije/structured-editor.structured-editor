package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import javax.swing.*;
import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 19:51
 */
public class PopupComponentChangedEvent extends EventObject {

    private JComponent popupComponent;

    /**
     * Source is a model that originated the event
     * @param source a model of structured editor
     */
    public PopupComponentChangedEvent(StructuredEditorModel source, JComponent popupComponent) {
        super(source);

        this.popupComponent = popupComponent;
    }

    public JComponent getPopupComponent() {
        return popupComponent;
    }
}
