package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteListComponent;
import ru.ipo.structurededitor.view.autocomplete.AutoCompletionSelectedEvent;
import ru.ipo.structurededitor.view.autocomplete.AutoCompletionSelectedListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 17:57
 */
public class AutoCompleteTextElement extends TextEditorElement implements PropertyChangeListener {

    private final AutoCompleteListComponent persistentPopupComponent;
    protected final EventListenerList listenerList = new EventListenerList();

    private static final KeyStroke[] showCompletion = {
            KeyStroke.getKeyStroke("control SPACE")
    };

    private static final KeyStroke[] selectionStrokes = {
            KeyStroke.getKeyStroke("ENTER"),
            KeyStroke.getKeyStroke("TAB"),
    };

    private static final KeyStroke[] hideSelectionStrokes = {
            KeyStroke.getKeyStroke("ESCAPE")
    };

    public AutoCompleteTextElement(StructuredEditorModel model, AutoCompleteElement... elements) {
        this(model, Arrays.asList(elements));
    }

    public AutoCompleteTextElement(StructuredEditorModel model, Collection<AutoCompleteElement> elements) {
        super(model, null, true);
        persistentPopupComponent = AutoCompleteListComponent.getComponent(elements, null);

        addPropertyChangeListener("focused", this);
    }

    /*@Override
    public void processKeyEvent(KeyEvent e) {
        if (isInKeyStrokesList(e, hideSelectionStrokes))
            setPopupComponent(null);
        else if (isInKeyStrokesList(e, showCompletion))
            setPopupComponent(persistentPopupComponent);
        else if (isInKeyStrokesList(e, selectionStrokes)) {
            fireAutoCompletionSelectedEvent(persistentPopupComponent.getSelectedElement());
            setPopupComponent(null);
        }
    }*/

    private boolean isInKeyStrokesList(KeyEvent e, KeyStroke[] keyStrokesList) {
        for (KeyStroke keyStroke : keyStrokesList) {
            if (KeyStroke.getKeyStrokeForEvent(e).equals(keyStroke))
                return true;
        }
        return false;
    }

    public void add(AutoCompletionSelectedListener listener) {
        listenerList.add(AutoCompletionSelectedListener.class, listener);
    }

    public void remove(AutoCompletionSelectedListener listener) {
        listenerList.remove(AutoCompletionSelectedListener.class, listener);
    }

    protected void fireAutoCompletionSelectedEvent(AutoCompleteElement selectedElement) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == AutoCompletionSelectedListener.class)
                ((AutoCompletionSelectedListener)listeners[i+1]).completionSelected(
                        new AutoCompletionSelectedEvent(this, selectedElement)
                );
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
//        if (evt.getPropertyName().equals("focused"))
//            setPopupComponent(null);
    }
}
