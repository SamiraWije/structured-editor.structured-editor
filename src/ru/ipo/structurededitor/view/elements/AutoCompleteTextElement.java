package ru.ipo.structurededitor.view.elements;

import org.w3c.dom.events.MouseEvent;
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

    private AutoCompleteListComponent persistentPopupComponent;
    private Object selectedValue = null;

    //This is a list of elements to complete. List is copied to persistentPopupComponent when it
    //is first created and after that is not used at all
    private List<AutoCompleteElement> elements;

    private static final KeyStroke KS_UP = KeyStroke.getKeyStroke("UP");
    private static final KeyStroke KS_DOWN = KeyStroke.getKeyStroke("DOWN");
    private static final KeyStroke KS_PG_UP = KeyStroke.getKeyStroke("PAGE_UP");
    private static final KeyStroke KS_PG_DOWN = KeyStroke.getKeyStroke("PAGE_DOWN");
    private static final KeyStroke KS_CLOSE = KeyStroke.getKeyStroke("ESCAPE");

    private final VisibleElementAction selectValueAction = new VisibleElementAction("Выбрать", "key.png", "ENTER") {
        @Override
        public void run(StructuredEditorModel model) {
            Object value = getValueThatWillBeSelected();
            setSelectedValue(value);

            getModel().hidePopup();

            updateShowPopupAction();
            updateSelectActionVisibility();
        }
    };

    private final VisibleElementAction showPopupAction = new VisibleElementAction("Выбрать вариант", "properties.png", "control SPACE") { //TODO get text from data
        @Override
        public void run(StructuredEditorModel model) {
            model.showPopup(getPersistentPopupComponent());
            updateShowPopupAction();
            updateSelectActionVisibility();
        }
    };

    public AutoCompleteTextElement(StructuredEditorModel model, AutoCompleteElement... elements) {
        this(model, Arrays.asList(elements));
    }

    public AutoCompleteTextElement(StructuredEditorModel model, List<AutoCompleteElement> elements) {
        super(model, null, true);

        this.elements = elements;

        addPropertyChangeListener(this); //will listen for "focused" and "text"

        updateShowPopupAction();
        updateSelectActionVisibility();
    }
    @Override
    protected void processMouseEvent(java.awt.event.MouseEvent e) {
        if (e.getID()== java.awt.event.MouseEvent.MOUSE_CLICKED){
            if (persistentPopupComponentIsShowing()) {
                    getModel().hidePopup();
                    updateSelectActionVisibility();
                    updateShowPopupAction();
                }
                else{
                    getModel().showPopup(getPersistentPopupComponent());

                }
        }
        super.processMouseEvent(e);
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
            boolean justPopped = false;
            if (text != null && !persistentPopupComponentIsShowing()) {
                popup();
                justPopped = true;
            }

            if (justPopped || persistentPopupComponentIsShowing()) {
                //get old list size
                Dimension oldPreferredSize = getPersistentPopupComponent().getPreferredSize();

                getPersistentPopupComponent().setSearchString(text);

                //reshow popup if size changed
                Dimension newPreferredSize = getPersistentPopupComponent().getPreferredSize();
                if (!oldPreferredSize.equals(newPreferredSize))
                    getModel().showPopup(getPersistentPopupComponent());
            }

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
        if (persistentPopupComponentIsShowing()) {
            if (ks.equals(KS_UP)) {
                getPersistentPopupComponent().moveSelection(-1, true);
                e.consume();
            } else if (ks.equals(KS_DOWN)) {
                getPersistentPopupComponent().moveSelection(+1, true);
                e.consume();
            } else if (ks.equals(KS_PG_UP)) {
                getPersistentPopupComponent().moveSelection(-AutoCompleteListComponent.VISIBLE_ELEMENTS_COUNT, false);
                e.consume();
            } else if (ks.equals(KS_PG_DOWN)) {
                getPersistentPopupComponent().moveSelection(+AutoCompleteListComponent.VISIBLE_ELEMENTS_COUNT, false);
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

        if (persistentPopupComponentIsShowing()) {
            //if popup is visible then select value form popup
            AutoCompleteElement selectedElement = getPersistentPopupComponent().getSelectedElement();
            if (selectedElement != null)
                result = selectedElement.getValue();
            if (result == null && getPersistentPopupComponent().getFilteredElementsCount() == 1) {
                AutoCompleteElement theOnlyElement = getPersistentPopupComponent().getElementAt(0);
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
        AutoCompleteElement elementByShortcut = getElementByShortcut(text);
        if (elementByShortcut != null)
            return elementByShortcut.getValue();
        else
            return null;
    }

    private AutoCompleteElement getElementByShortcut(String text) {
        if (persistentPopupComponent != null)
            return persistentPopupComponent.getElementByShortcut(text);
        else {
            //this will never occur, by the way
            for (AutoCompleteElement element : elements)
                if (element.getShortcut().equals(text))
                    return element;
            return null;
        }
    }

    private void updateSelectActionVisibility() {
        Object value = getValueThatWillBeSelected();
        if (value != null)
            addActionToTheBeginning(selectValueAction);
        else
            removeAction(selectValueAction);
    }

    private void updateShowPopupAction() {
        if (persistentPopupComponentIsShowing())
            removeAction(showPopupAction);
        else
            addAction(showPopupAction);
    }

    private boolean persistentPopupComponentIsShowing() {
        return persistentPopupComponent != null && persistentPopupComponent.isShowing();
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

    //this method creates a component if it does not exist, so don't call it if component is not really needed
    public AutoCompleteListComponent getPersistentPopupComponent() {
        if (persistentPopupComponent == null) {
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

            //let gc dispose the list
            elements = null;
        }

        return persistentPopupComponent;
    }

}
