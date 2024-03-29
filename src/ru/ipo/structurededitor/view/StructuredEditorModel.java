package ru.ipo.structurededitor.view;

import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.ModificationHistory;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.view.elements.VisibleElement;
import ru.ipo.structurededitor.view.events.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.datatransfer.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 * Корень дерева ячеек
 */
public class StructuredEditorModel implements ClipboardOwner {

    private final CaretEvent caretEvent = new CaretEvent(this);

    public void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }
    public void lostOwnership( Clipboard aClipboard, Transferable aContents) {
     //do nothing
   }
    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText =
                (contents != null) &&
                        contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex) {
                //highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        return result;
    }

    public Object getApp() {
        return app;
    }

    public void setApp(Object app) {
        this.app = app;
        updateModel();
    }

    private Object app;

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
        updateModel();
    }

    private boolean view = false;
    private int absoluteCaretX = 0;
    private int absoluteCaretY = 0;

    public int getAbsoluteCaretX() {
        return absoluteCaretX;
    }

    public int getAbsoluteCaretY() {
        return absoluteCaretY;
    }

    public void setAbsoluteCaretPosition(int col, int line) {
        this.absoluteCaretX = col;
        this.absoluteCaretY = line;

        //test if other component should be focused
        if (focusedElement == null || !elementContainsPoint(focusedElement, line, col))
            setFocusedElement(findElementByPosition(line, col));

        repaint();

        fireCaretShow(caretEvent);
    }

    private VisibleElement rootElement;
    //private StructuredEditor editor;
    private VisibleElement focusedElement;

    public EditorsRegistry getEditorsRegistry() {
        return editorsRegistry;
    }

    public void setEditorsRegistry(EditorsRegistry editorsRegistry) {
        this.editorsRegistry = editorsRegistry;
        updateModel();
    }

    private EditorsRegistry editorsRegistry;

    private DSLBeansRegistry beansRegistry;

    public DSLBeansRegistry getBeansRegistry() {
        return beansRegistry;
    }

    public void setBeansRegistry(DSLBeansRegistry beansRegistry) {
        this.beansRegistry = beansRegistry;
        updateModel();
    }

    private DSLBean o;

    public StructuredEditorModel(DSLBean o) {
        this(o, new ModificationHistory());
    }

    public StructuredEditorModel(DSLBean o, ModificationHistory modificationHistory) {
        this.o = o;
        this.modificationHistory = modificationHistory;
        editorsRegistry = new EditorsRegistry();
        beansRegistry = new DSLBeansRegistry();

        updateModel();
    }

    public void updateModel() {
        setRootElement(new EditorRenderer(this, o).getRenderResult());
    }

    public void setObject(DSLBean o) {
        this.o = o;
        updateModel();
    }

    public DSLBean getObject() {
        return o;
    }

    public ModificationHistory getModificationHistory() {
        return modificationHistory;
    }

    private ModificationHistory modificationHistory;

    private EventListenerList listenerList = new EventListenerList();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Добавление слушателя для события "изменение любого свойства класса"
     *
     * @param listener Слушатель
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Добавление слушателя для события "изменение конкретного свойства класса"
     *
     * @param propertyName Имя свойства
     * @param listener     Слушатель
     */
    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void addCaretListener(CaretListener l) {
        listenerList.add(CaretListener.class, l);
    }

    public void removeCaretListener(CaretListener l) {
        listenerList.remove(CaretListener.class, l);
    }

    public void addRepaintListener(RepaintListener l) {
        listenerList.add(RepaintListener.class, l);
    }

    public void removeRepaintListener(RepaintListener l) {
        listenerList.remove(RepaintListener.class, l);
    }
    public void addImageLoadListener(ImageLoadListener l) {
        listenerList.add(ImageLoadListener.class, l);
    }

    public void removeImageLoadListener(ImageLoadListener l) {
        listenerList.remove(ImageLoadListener.class, l);
    }
    public void repaint() {
        fireRepaint();
    }

    protected void fireRepaint() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == RepaintListener.class) {
                ((RepaintListener) listeners[i + 1]).repaint();
            }
        }
    }

     public Image loadImage(String fileName) {
        return fireImageLoad(new ImageLoadEvent(this,fileName));
    }

    protected Image fireImageLoad(ImageLoadEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ImageLoadListener.class) {
                return ((ImageLoadListener) listeners[i + 1]).loadImage(event);
            }
        }
        return null;
    }
    protected String fireImageLoad() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ImageLoadListener.class) {
                return ((ImageLoadListener) listeners[i + 1]).selectImage();
            }
        }
        return null;
    }



    protected void fireCaretShow(CaretEvent ce) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CaretListener.class) {
                // Lazily create the event:
                /*if (Event == null)
             fooEvent = new FooEvent(this);*/
                ((CaretListener) listeners[i + 1]).showCaret(ce);
                //return;
            }
        }
    }

    public VisibleElement getFocusedElement() {
        return focusedElement;
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    /**
     * Корень отображаемого элемента соответствующего корню дерева ячеек
     *
     * @return
     */
    public VisibleElement getRootElement() {
        return rootElement;
    }

    /*public StructuredEditorUI getUI() {
        if (editor != null)
            return editor.getUI();
        else
            return null;
    } */
    // PropertyChangeSupport

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Установить активный элемент и вызвать всех подписанных слушателей
     *
     * @param focusedElement element to set focus to
     */

    private void setFocusedElement(VisibleElement focusedElement) {
        if (focusedElement == this.focusedElement)
            return;

        // Фокус может иметь только элемент без дочерних элементов
        // Спускаемся к самому вложенному элементу
        if (focusedElement != null) {
            while (focusedElement.getChildrenCount() != 0)
                focusedElement = focusedElement.getChild(0);
        }

        VisibleElement oldValue = this.focusedElement;
        this.focusedElement = focusedElement;
        pcs.firePropertyChange("focusedElement", oldValue, focusedElement);

        if (oldValue != null) {
            oldValue.fireFocusChanged(true);
        }
        if (focusedElement != null) {
            focusedElement.fireFocusChanged(false);
        }

        repaint();
    }

    public void setRootElement(VisibleElement rootElement) {
        this.rootElement = rootElement;
        setFocusedElement(rootElement);
    }

    public void moveCaretToElement(VisibleElement visibleElement) {
        TextPosition tp = visibleElement.getAbsolutePosition();
        setAbsoluteCaretPosition(tp.getColumn(), tp.getLine());
    }

    //popup component changed event: add, remove, fire

    protected void firePopupComponentChangedEvent(JComponent popupComponent) {
        PopupComponentChangedEvent event = new PopupComponentChangedEvent(this, popupComponent);

        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PopupComponentChangedListener.class) {
                ((PopupComponentChangedListener) listeners[i + 1]).componentChanged(event);
            }
        }
    }

    public void addPopupComponentChangedListener(PopupComponentChangedListener listener) {
        listenerList.add(PopupComponentChangedListener.class, listener);
    }

    public void removePopupComponentChangedListener(PopupComponentChangedListener listener) {
        listenerList.remove(PopupComponentChangedListener.class, listener);
    }

    public void showPopup(JComponent popupComponent) {
        firePopupComponentChangedEvent(popupComponent);
    }

    public void hidePopup() {
        firePopupComponentChangedEvent(null);
    }

    private boolean elementContainsPoint(VisibleElement element, int line, int column) {
        TextPosition position = element.getAbsolutePosition();

        int line0 = position.getLine();
        int column0 = position.getColumn();

        int w = element.getWidth();
        int h = element.getHeight();

        return line0 <= line &&
                line < line0 + h &&
                column0 <= column &&
                column <= column0 + w;
    }

    public VisibleElement findElementByPosition(int line, int column) {
        VisibleElement element = getRootElement();

        if (!elementContainsPoint(element, line, column))
            return null;

        goDown:
        while (true) {
            int childrenCount = element.getChildrenCount();
            if (childrenCount == 0)
                return element;

            for (int i = 0; i < childrenCount; i++) {
                VisibleElement child = element.getChild(i);
                if (elementContainsPoint(child, line, column)) {
                    element = child;
                    continue goDown;
                }
            }

            return null;
        }
    }

    //visible actions changed

    public void fireVisibleElementActionsChangedEvent(VisibleElement element) {
        VisibleElementActionsChangedEvent event = new VisibleElementActionsChangedEvent(this, element);

        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == VisibleElementActionsChangedListener.class) {
                ((VisibleElementActionsChangedListener) listeners[i + 1]).actionsChanged(event);
            }
        }
    }

    public void addVisibleElementActionsChangedListener(VisibleElementActionsChangedListener listener) {
        listenerList.add(VisibleElementActionsChangedListener.class, listener);
    }

    public void removeVisibleElementActionsChangedListener(VisibleElementActionsChangedListener listener) {
        listenerList.remove(VisibleElementActionsChangedListener.class, listener);
    }

    public void testFocus() {
        VisibleElement e = focusedElement;

        while (true) {
            if (e == rootElement)
                return;
            if (e == null) {
                focusedElement = null;
                return;
            }
            e = e.getParent();
        }
    }

    public String selectImage() {
        return fireImageLoad();
    }
}