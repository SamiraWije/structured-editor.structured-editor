package ru.ipo.structurededitor.view;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.actions.ActionsListComponent;
import ru.ipo.structurededitor.actions.VisibleElementAction;
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
    private ActionListener caretBlinkListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO implement normal blinking
            caretVisible = !caretVisible;
            editor.getModel().repaint();
        }
    };
    private Timer caretBlinkTimer = new Timer(600, caretBlinkListener);

    private PopupSupport popupSupport;

    private static HashMap<JComponent, StructuredEditorUI> editor2ui = new HashMap<JComponent, StructuredEditorUI>();

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

        caretBlinkTimer.start();

        editor.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        //add listeners
        editor.getModel().addPropertyChangeListener("focusedElement",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        popupSupport.hide();

                        updateAvailableActions();

                        redrawEditor();
                    }
                });

        editor.getModel().addVisibleElementActionsChangedListener(new VisibleElementActionsChangedListener() {
            @Override
            public void actionsChanged(VisibleElementActionsChangedEvent e) {
                if (e.getElement() == editor.getModel().getFocusedElement())
                    updateAvailableActions();
            }
        });

        editor.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                redrawEditor();
            }

            public void focusLost(FocusEvent e) {
                redrawEditor();
            }
        });

        editor.getModel().addCaretListener(new CaretListener() {
            public void showCaret(CaretEvent evt) {
                //TODO rewrite
                if (!editor.hasFocus() || !caretVisible)
                    return;
                Graphics g = evt.getD().getGraphics();

                g.setColor(Color.BLUE);
                int x0 = xToPixels(editor.getModel().getAbsoluteCaretX());
                int y0 = yToPixels(editor.getModel().getAbsoluteCaretY());
                int y1 = y0 + getCharHeight();
                g.drawLine(x0-1, y0, x0-1, y1);

                //TODO scroll
                //editor.scrollRectToVisible(new Rectangle(x0, y0, 2, y1 - y0));
            }
        });

        editor.getModel().addRepaintListener(new RepaintListener() {
            public void repaint() {
                redrawEditor();
            }
        });

        editor.getModel().addPopupComponentChangedListener(new PopupComponentChangedListener() {
            @Override
            public void componentChanged(PopupComponentChangedEvent event) {
                StructuredEditorModel model = editor.getModel();
                VisibleElement element = model.getFocusedElement();
                JComponent component = event.getPopupComponent();
                if (element == null || component == null) {
                    popupSupport.hide();
                    return;
                }

                TextPosition position = element.getAbsolutePosition();
                Point locationOnScreen = editor.getLocationOnScreen();

                popupSupport.show(
                        component,
                        xToPixels(position.getColumn()) + (int)locationOnScreen.getX(),
                        yToPixels(position.getLine() + element.getHeight()) + (int)locationOnScreen.getY()
                );
            }
        });

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                caretBlinkTimer.restart();
                caretVisible = true;
                redrawEditor();
            }
        });
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

    @Override
    public void uninstallUI(JComponent c) {
        //TODO uninstall everything
        caretBlinkTimer.removeActionListener(caretBlinkListener);
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

        if (se.isFocusOwner() && !se.isView())
            se.getModel().showCaret(d);
        /*if (focusedRectangle != null) {
            g.setColor(Color.blue);
            g.drawRect(focusedRectangle.x, focusedRectangle.y, focusedRectangle.width, focusedRectangle.height);
        }*/
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
}
