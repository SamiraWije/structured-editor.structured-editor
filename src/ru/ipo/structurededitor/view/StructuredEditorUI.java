package ru.ipo.structurededitor.view;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.actions.ActionsListComponent;
import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteListComponent;
import ru.ipo.structurededitor.view.elements.VisibleElement;
import ru.ipo.structurededitor.view.events.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: Ilya Date: 02.01.2010 Time: 16:59:16
 */
public class StructuredEditorUI extends ComponentUI {

    protected StructuredEditor editor;

    private int charHeight;
    private int charWidth;
    private int charDescent;
    private int charAscent;

    private int horizontalMargin;
    private int verticalMargin;

    private boolean caretVisible = true;
    private Timer caretBlinkTimer;

    private PopupSupport popupSupport;

    private static HashMap<JComponent, StructuredEditorUI> editor2ui = new HashMap<JComponent, StructuredEditorUI>();

    //listeners
    private ActionListener caretBlinkListener;
    private PropertyChangeListener focusedElementChangedListener;
    private VisibleElementActionsChangedListener actionsChangedListener;
    private FocusListener componentFocusListener;
    private CaretListener caretMovedListener;
    private RepaintListener repaintNeededListener;
    private PopupComponentChangedListener popupListener;
    private KeyAdapter componentKeyListener;
    private ShowActionsPopup tipPopup;

    public StructuredEditorUI() {
        createListeners();
    }

    public static ComponentUI createUI(JComponent component) {
        StructuredEditorUI ui = editor2ui.get(component);
        if (ui == null) {
            ui = new StructuredEditorUI();
            editor2ui.put(component, ui);
        }
        return ui;
    }

    public int getCharAscent() {
        return charAscent;
    }

    public int getCharDescent() {
        return charDescent;
    }

    public int getCharHeight() {
        return charHeight;
    }

    public int getCharWidth() {
        return charWidth;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        VisibleElement root = editor.getModel().getRootElement();
        int width = 2 * horizontalMargin + root.getWidth() * getCharWidth();
        int height = 2 * verticalMargin + root.getHeight() * getCharHeight()
                + getCharDescent();
        return new Dimension(width, height);
    }

    @Override
    public void installUI(JComponent c) {
        c.setBackground(Color.white);
        c.setOpaque(true);
        c.setFont(UIManager.getFont("StructuredEditor.font"));

        editor = (StructuredEditor) c;
        popupSupport = new PopupSupport(editor);

        FontMetrics fontMetrics = c.getFontMetrics(UIManager.getFont("StructuredEditor.font"));
        charHeight = fontMetrics.getHeight();
        charWidth = fontMetrics.charWidth('m');
        charDescent = fontMetrics.getDescent();
        charAscent = fontMetrics.getAscent();

        horizontalMargin = UIManager.getInt("StructuredEditor.horizontalMargin");
        verticalMargin = UIManager.getInt("StructuredEditor.verticalMargin");

        caretBlinkTimer = new Timer(600, caretBlinkListener);
        caretBlinkTimer.start();

        editor.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        tipPopup = new ShowActionsPopup(editor);

        //add listeners
        editor.getModel().addPropertyChangeListener("focusedElement", focusedElementChangedListener);
        editor.getModel().addVisibleElementActionsChangedListener(actionsChangedListener);
        editor.addFocusListener(componentFocusListener);
        editor.getModel().addCaretListener(caretMovedListener);
        editor.getModel().addRepaintListener(repaintNeededListener);
        editor.getModel().addPopupComponentChangedListener(popupListener);
        editor.addKeyListener(componentKeyListener);
    }

    @Override
    public void uninstallUI(JComponent c) {
        //TODO uninstall colors and cursors
        caretBlinkTimer.removeActionListener(caretBlinkListener);
        caretBlinkTimer.stop();

        editor.getModel().removePropertyChangeListener("focusedElement", focusedElementChangedListener);
        editor.getModel().removeVisibleElementActionsChangedListener(actionsChangedListener);
        editor.removeFocusListener(componentFocusListener);
        editor.getModel().removeCaretListener(caretMovedListener);
        editor.getModel().removeRepaintListener(repaintNeededListener);
        editor.getModel().removePopupComponentChangedListener(popupListener);
        editor.removeKeyListener(componentKeyListener);
    }

    private void updateAvailableActions() {
        VisibleElement focusedElement = editor.getModel().getFocusedElement();
        Collection<? extends VisibleElementAction> actions;
        if (focusedElement == null)
            actions = new ArrayList<VisibleElementAction>(0);
        else
            actions = focusedElement.getAllAvailableActions();
        ActionsListComponent actionsListComponent = editor.getActionsListComponent();

        actionsListComponent.clearActions();
        for (VisibleElementAction action : actions)
            actionsListComponent.addAction(action);
    }

    /**
     * Отрисовка всего поля на компоненте
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        StructuredEditor se = (StructuredEditor) c;
        VisibleElement element = se.getModel().getRootElement();

        Display d = new Display(g, this);

        //get focused rectangle
        VisibleElement focusedElement = se.getModel().getFocusedElement();
        Rectangle focusedRectangle = null;
        //TODO highlight only elements that have actions or react to keystrokes
        if (focusedElement != null && !se.isView() /*&& editor.isFocusOwner()*/) {
            TextPosition fpos = focusedElement.getAbsolutePosition();

            focusedRectangle = new Rectangle(d.xToPixels(fpos.getColumn()), d
                    .yToPixels(fpos.getLine()), getCharWidth()
                    * focusedElement.getWidth(), getCharHeight()
                    * focusedElement.getHeight());
        }

        //draw focused element
        if (focusedRectangle != null && editor.hasFocus()) {
            g.setColor(UIManager.getColor("StructuredEditor.focusedColor"));
            g.fillRect(focusedRectangle.x, focusedRectangle.y,
                    focusedRectangle.width, focusedRectangle.height);
        }

        //draw element
        element.drawElement(0, 0, d);

        if (se.isFocusOwner() && !se.isView() && caretVisible) {
            g.setColor(Color.BLUE);
            int x0 = xToPixels(editor.getModel().getAbsoluteCaretX());
            int y0 = yToPixels(editor.getModel().getAbsoluteCaretY());
            int y1 = y0 + getCharHeight();
            g.drawLine(x0 - 1, y0, x0 - 1, y1);
        }
        if (!editor.hasFocus()){
           editor.getModel().hidePopup();
        }
    }

    /**
     * Заставит редактор перерисоваться
     */
    public void redrawEditor() {
        editor.revalidate();
        editor.repaint();
    }

    public int xToPixels(int x) {
        return horizontalMargin + x * getCharWidth();
    }

    public int yToPixels(int y) {
        return verticalMargin + y * getCharHeight();
    }

    public int pixelsToX(int x) {
        return (x - horizontalMargin) / getCharWidth();
    }

    public int pixelsToY(int y) {
        return (y - verticalMargin) / getCharHeight();
    }

    private void popupTipIfNeeded() {
        /*StructuredEditorModel model = editor.getModel();
        VisibleElement focusedElement = model.getFocusedElement();
        if (focusedElement == null)
            return;
        if (!editor.getActionsListComponent().hasAvailableActions())
            return;

        TextPosition position = focusedElement.getAbsolutePosition();
        Point locationOnScreen = editor.getLocationOnScreen();

        popupSupport.show(
                tipPopup,
                xToPixels(position.getColumn() + focusedElement.getWidth()) + locationOnScreen.x + 2,
                yToPixels(position.getLine()) + locationOnScreen.y
        );*/
    }

    private void createListeners() {
        caretBlinkListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO implement normal blinking
                caretVisible = !caretVisible;
                editor.getModel().repaint();
            }
        };

        focusedElementChangedListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                popupSupport.hide();

                updateAvailableActions();

                popupTipIfNeeded();

                redrawEditor();
            }
        };

        actionsChangedListener = new VisibleElementActionsChangedListener() {
            @Override
            public void actionsChanged(VisibleElementActionsChangedEvent e) {
                if (e.getElement() == editor.getModel().getFocusedElement())
                    updateAvailableActions();
            }
        };

        componentFocusListener = new FocusListener() {
            public void focusGained(FocusEvent e) {
                redrawEditor();
            }

            public void focusLost(FocusEvent e) {
                redrawEditor();
            }
        };

        caretMovedListener = new CaretListener() {
            public void showCaret(CaretEvent evt) {
                //scroll component to make caret visible

                int x0 = xToPixels(editor.getModel().getAbsoluteCaretX());
                int y0 = yToPixels(editor.getModel().getAbsoluteCaretY());
                int y1 = y0 + getCharHeight();

                Rectangle visibleRect = editor.getVisibleRect();

                //TODO think of all constants, especially about 2
                if (!visibleRect.contains(x0 - 2, y0 - 2) || !visibleRect.contains(x0 + 2, y1 + 2))
                    editor.scrollRectToVisible(new Rectangle(
                            x0 - charWidth * 4,
                            y0 - 4 * charHeight,
                            charWidth * 8,
                            y1 - y0 + 9 * charHeight
                    ));
            }
        };

        repaintNeededListener = new RepaintListener() {
            public void repaint() {
                redrawEditor();
            }
        };

        popupListener = new PopupComponentChangedListener() {
            @Override
            public void componentChanged(PopupComponentChangedEvent event) {
                StructuredEditorModel model = editor.getModel();
                VisibleElement element = model.getFocusedElement();
                JComponent component = event.getPopupComponent();
                if (element == null || component == null) {
                    popupSupport.hide();
                    popupTipIfNeeded();
                    return;
                }

                TextPosition position = element.getAbsolutePosition();
                Point locationOnScreen = editor.getLocationOnScreen();

                if (component instanceof AutoCompleteListComponent)
                    component.setMinimumSize(new Dimension(element.getWidth() * getCharWidth(), 0));

                popupSupport.show(
                        component,
                        xToPixels(position.getColumn()) + (int) locationOnScreen.getX(),
                        yToPixels(position.getLine() + element.getHeight()) + (int) locationOnScreen.getY()
                );
            }
        };

        componentKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                caretBlinkTimer.restart();
                caretVisible = true;
                redrawEditor();
            }
        };
    }
}
