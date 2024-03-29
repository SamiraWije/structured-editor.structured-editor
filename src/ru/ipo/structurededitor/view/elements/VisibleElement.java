package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * View часть ячейки (Cell)
 */
public abstract class VisibleElement {

    //private ArrayList<ContentChangedEventListener> listeners = new ArrayList<ContentChangedEventListener>();
    private VisibleElement parent;
    private final StructuredEditorModel model;
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    protected ArrayList<KeyListener> keyListeners = new ArrayList<KeyListener>();

    private int width;
    private int height;

    private List<VisibleElementAction> actions = new ArrayList<VisibleElementAction>();

    private String toolTipText = null;

    protected VisibleElement(StructuredEditorModel model) {
        this.model = model;
    }

    //key listeners

    public void addKeyListener(KeyListener listener) {
        keyListeners.add(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public abstract void drawElement(int x0, int y0, Display d);

    public boolean isView() {
        return getModel().isView();
    }

    /**
     * Обработка нажатия клавиш
     *
     * @param e key event
     */

    public void fireKeyEvent(KeyEvent e) {
        //let all listeners process the event
        for (int i = keyListeners.size() - 1; i >= 0; i--) {
            KeyListener l = keyListeners.get(i);
            switch (e.getID()) {
                case KeyEvent.KEY_PRESSED:
                    l.keyPressed(e);
                    break;
                case KeyEvent.KEY_RELEASED:
                    l.keyReleased(e);
                    break;
                case KeyEvent.KEY_TYPED:
                    l.keyTyped(e);
                    break;
            }
            if (e.isConsumed())
                return;
        }

        //let the element itself process the event
        if (e.getID() != KeyEvent.KEY_PRESSED)
            return;
        if (!e.isConsumed())
            processKeyEvent(e);
    }

    public void fireMouseEvent(MouseEvent e) {
        processMouseEvent(e);
    }
     public void fireMouseMotionEvent(MouseEvent e) {
        processMouseMotionEvent(e);
    }
    public TextPosition getAbsolutePosition() {
        int line = 0;
        int column = 0;
        VisibleElement cur = this;
        while (cur != null) {
            VisibleElement parent = cur.getParent();
            int x = 0;
            int y = 0;
            if (parent != null) {
                TextPosition tp = parent.getChildPosition(cur);
                if (tp == null)
                    throw new RuntimeException("Trying to evaluate position of not rooted visible element");
                x = tp.getColumn();
                y = tp.getLine();
            }

            line += y;
            column += x;

            cur = parent;
        }

        return new TextPosition(line, column);
    }

    public VisibleElement getChild(int index) {
        return null;
    }


    public int getChildIndex(VisibleElement child) {
        for (int i = 0; i < getChildrenCount(); i++)
            if (getChild(i) == child)
                return i;

        return -1;
    }

    /**
     * This method should normally be overridden
     *
     * @param index child index
     * @return position of child
     */
    public TextPosition getChildPosition(int index) {
        return null;
    }

    public TextPosition getChildPosition(VisibleElement child) {
        for (int i = 0; i < getChildrenCount(); i++)
            if (getChild(i) == child)
                return getChildPosition(i);

        return null;
    }

    public int getChildrenCount() {
        return 0;
    }

    public int getHeight() {
        return height;
    }

    public StructuredEditorModel getModel() {
        return model;
    }

    public VisibleElement getParent() {
        return parent;
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    public int getWidth() {
        return width;
    }

    public boolean isFocused() {
        return model.getFocusedElement() == this;
    }

    //TODO invent some better way for this
    public void fireFocusChanged(boolean oldFocused) {
        pcs.firePropertyChange("focused", oldFocused, isFocused());
    }

    public boolean isParentOf(VisibleElement element) {
        while (element != null) {
            if (element == this)
                return true;
            element = element.getParent();
        }

        return false;
    }

    public void repaint() {
        getModel().repaint();
    }

    protected void setHeight(int height) {
        int oldValue = this.height;
        this.height = height;
        pcs.firePropertyChange("height", oldValue, height);
    }

    public void setParent(VisibleElement parent) {
        this.parent = parent;
    }

    protected void setWidth(int width) {
        int oldValue = this.width;
        this.width = width;
        pcs.firePropertyChange("width", oldValue, width);
    }

    protected void processKeyEvent(KeyEvent e) {
    }

    //TODO make use of mouse events
    @SuppressWarnings({"UnusedParameters"})
    protected void processMouseEvent(MouseEvent e) {
    }
    @SuppressWarnings({"UnusedParameters"})
    protected void processMouseMotionEvent(MouseEvent e) {
    }
    public void removeKeyListener(KeyListener listener) {
        keyListeners.remove(listener);
    }

    //------------------- PropertyChangedSupport --------

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    //------------------ Actions List -----------------
    public void addActionToTheBeginning(VisibleElementAction action) {
        if (!actions.contains(action)) {
            actions.add(0, action);
            model.fireVisibleElementActionsChangedEvent(this);
        }
    }

    public void addAction(VisibleElementAction action) {
        if (action == null)
            throw new IllegalArgumentException("Action may not be null");

        if (!actions.contains(action)) {
            actions.add(action);
            model.fireVisibleElementActionsChangedEvent(this);
        }
    }

    public void removeAction(VisibleElementAction action) {
        if (action == null)
            throw new IllegalArgumentException("Action may not be null");

        if (actions.contains(action)) {
            actions.remove(action);
            model.fireVisibleElementActionsChangedEvent(this);
        }
    }

    public void clearActions() {
        if (actions.size() > 0) {
            actions.clear();
            model.fireVisibleElementActionsChangedEvent(this);
        }
    }

    public Collection<? extends VisibleElementAction> getActions() {
        return actions;
    }

    /**
     * Returns actions that are available from this element and from all its containers
     *
     * @return collection of actions
     */
    public Collection<? extends VisibleElementAction> getAllAvailableActions() {
        List<VisibleElementAction> allActions = new ArrayList<VisibleElementAction>();

        VisibleElement element = this;
        while (element != null) {
            allActions.addAll(element.getActions());

            element = element.getParent();
        }

        return allActions;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }
}