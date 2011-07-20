package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteListComponent;
import ru.ipo.structurededitor.view.events.AutoCompleteElementSelectedEvent;
import ru.ipo.structurededitor.view.events.AutoCompleteElementSelectedListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 17:57
 */
public class AutoCompleteTextElement extends TextEditorElement implements PropertyChangeListener {

    private final AutoCompleteListComponent persistentPopupComponent;
    private Object selectedValue = null;

    private static final KeyStroke KS_UP = KeyStroke.getKeyStroke("UP");
    private static final KeyStroke KS_DOWN = KeyStroke.getKeyStroke("DOWN");
    private static final KeyStroke KS_PG_UP = KeyStroke.getKeyStroke("PAGE_UP");
    private static final KeyStroke KS_PG_DOWN = KeyStroke.getKeyStroke("PAGE_DOWN");
    private static final KeyStroke KS_CLOSE = KeyStroke.getKeyStroke("ESCAPE");

    private final VisibleElementAction selectValueAction = new VisibleElementAction("Выбрать", "key.png", KeyStroke.getKeyStroke("ENTER")) {
        @Override
        public void run(StructuredEditorModel model) {
            Object value = getValueThatWillBeSelected();
            setSelectedValue(value);

            getModel().hidePopup();

            updateShowPopupAction();
            updateSelectActionVisibility();
        }
    };

    private final VisibleElementAction showPopupAction = new VisibleElementAction("Выбрать вариант", "properties.png", KeyStroke.getKeyStroke("control SPACE")) { //TODO get text from data
        @Override
        public void run(StructuredEditorModel model) {
            model.showPopup(persistentPopupComponent);
            updateShowPopupAction();
            updateSelectActionVisibility();
        }
    };

    public AutoCompleteTextElement(StructuredEditorModel model, AutoCompleteElement... elements) {
        this(model, Arrays.asList(elements));
    }

    public AutoCompleteTextElement(StructuredEditorModel model, List<AutoCompleteElement> elements) {
        super(model, null, true);

        persistentPopupComponent = AutoCompleteListComponent.getComponent(elements, null);
        persistentPopupComponent.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateSelectActionVisibility();
                }
            }
        });

        persistentPopupComponent.addAutoCompleteElementSelectedListener(new AutoCompleteElementSelectedListener() {
            @Override
            public void elementChanged(AutoCompleteElementSelectedEvent e) {
                AutoCompleteElement element = e.getElement();
                if (element != null)
                    selectValueAction.run(getModel());
            }
        });

        addPropertyChangeListener("focused", this);
        addPropertyChangeListener("text", this);

        updateShowPopupAction();
        updateSelectActionVisibility();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        if (propName.equals("focused")) {
            setText(null);
            updateShowPopupAction();
        } else if (propName.equals("text")) {
            String text = getText();

            //show completion variants if user is typing
            if (text != null && !persistentPopupComponent.isShowing())
                popup();

            //get old list size
            Dimension oldPreferredSize = persistentPopupComponent.getPreferredSize();

            persistentPopupComponent.setSearchString(text);

            //reshow popup if size changed
            Dimension newPreferredSize = persistentPopupComponent.getPreferredSize();
            if (persistentPopupComponent.isShowing() && !oldPreferredSize.equals(newPreferredSize))
                getModel().showPopup(persistentPopupComponent);

            updateSelectActionVisibility();
        }
    }

    public void popup() {
        showPopupAction.run(getModel());
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);

        //several key events should be directed to popup component if it is shown
        if (persistentPopupComponent.isShowing()) {
            if (ks.equals(KS_UP)) {
                persistentPopupComponent.moveSelection(-1, true);
                e.consume();
            } else if (ks.equals(KS_DOWN)) {
                persistentPopupComponent.moveSelection(+1, true);
                e.consume();
            } else if (ks.equals(KS_PG_UP)) {
                persistentPopupComponent.moveSelection(-AutoCompleteListComponent.VISIBLE_ELEMENTS_COUNT, false);
                e.consume();
            } else if (ks.equals(KS_PG_DOWN)) {
                persistentPopupComponent.moveSelection(+AutoCompleteListComponent.VISIBLE_ELEMENTS_COUNT, false);
                e.consume();
            } else if (ks.equals(KS_CLOSE)) {
                getModel().hidePopup();
                updateSelectActionVisibility();
                updateShowPopupAction();
                e.consume();
            }
        }

        if (!e.isConsumed())
            super.processKeyEvent(e);
    }

    private Object getValueThatWillBeSelected() {
        Object result = null;

        if (persistentPopupComponent.isShowing()) {
            //if popup is visible then select value form popup
            AutoCompleteElement selectedElement = persistentPopupComponent.getSelectedElement();
            if (selectedElement != null)
                result = selectedElement.getValue();
            if (result == null && persistentPopupComponent.getFilteredElementsCount() == 1) {
                AutoCompleteElement theOnlyElement = persistentPopupComponent.getElementAt(0);
                if (theOnlyElement != null)
                    result = theOnlyElement.getValue();
            }
        }

        if (result != null)
            return result;

        //if no popup visible or nothing is selected, then try to select by entered text
        String text = getText();
        if (text == null)
            return null;
        AutoCompleteElement elementByShortcut = persistentPopupComponent.getElementByShortcut(text);
        if (elementByShortcut != null)
            return elementByShortcut.getValue();
        else
            return null;
    }

    private void updateSelectActionVisibility() {
        Object value = getValueThatWillBeSelected();
        if (value != null)
            addActionToTheBeginning(selectValueAction);
        else
            removeAction(selectValueAction);
    }

    private void updateShowPopupAction() {
        if (persistentPopupComponent.isShowing())
            removeAction(showPopupAction);
        else
            addAction(showPopupAction);
    }

    public void setSelectedValue(Object selectedValue) {
        Object oldValue = this.selectedValue;
        this.selectedValue = selectedValue;
        pcs.firePropertyChange("selectedValue", oldValue, selectedValue);
    }

    public Object getSelectedValue() {
        return selectedValue;
    }

    public void setShowPopupActionText(String actionText) {
        showPopupAction.setActionText(actionText);
    }
}
