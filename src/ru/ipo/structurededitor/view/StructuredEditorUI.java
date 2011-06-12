package ru.ipo.structurededitor.view;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.view.elements.VisibleElement;
import ru.ipo.structurededitor.view.events.*;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Ilya Date: 02.01.2010 Time: 16:59:16
 */
public class StructuredEditorUI extends ComponentUI {

    protected StructuredEditor editor;
    public static final int HORIZONTAL_MARGIN = 0;
    public static final int VERTICAL_MARGIN = 0;
    public static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    private int charHeight;
    private int charWidth;
    private int charDescent;
    private int charAscent;

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
        int width = 2 * HORIZONTAL_MARGIN + root.getWidth() * getCharWidth();
        int height = 2 * VERTICAL_MARGIN + root.getHeight() * getCharHeight()
                + getCharDescent();
        return new Dimension(width, height);
    }

    @Override
    public void installUI(JComponent c) {
        c.setBackground(Color.white);
        c.setOpaque(true);
        c.setFont(FONT);

        editor = (StructuredEditor) c;

        //add listeners
        editor.getModel().addPropertyChangeListener("focusedElement",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        redrawEditor();
                    }
                });

        editor.getModel().addPopupListener(new PopupListener() {
            public ListDialog showPopup(PopupEvent evt) {
                int x = evt.getX();
                int y = evt.getY();
                Vector<String> filteredPopupList = evt.getFilteredPopupList();

                x = xToPixels(x) + editor.getLocationOnScreen().x;
                y = yToPixels(y) + editor.getLocationOnScreen().y;
                ListDialog dialog = new ListDialog(editor, filteredPopupList.toArray(),
                        filteredPopupList.get(0), evt.getLongStr(), x, y);
                redrawEditor();
                return dialog;
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
                evt.getD().getGraphics().setColor(Color.BLUE);
                evt.getD().getGraphics().drawLine(
                        xToPixels(editor.getModel().getAbsoluteCaretX()),
                        yToPixels(editor.getModel().getAbsoluteCaretY()),
                        xToPixels(editor.getModel().getAbsoluteCaretX()),
                        yToPixels(editor.getModel().getAbsoluteCaretY()) + getCharHeight());
            }
        });
        editor.getModel().addRepaintListener(new RepaintListener() {
            public void repaint() {
                redrawEditor();
            }
        });
        FontMetrics fontMetrics = c.getFontMetrics(FONT);
        charHeight = fontMetrics.getHeight();
        charWidth = fontMetrics.charWidth('m');
        charDescent = fontMetrics.getDescent();
        charAscent = fontMetrics.getAscent();
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
        if (focusedElement != null && !se.isView() /*&& editor.isFocusOwner()*/) {
            TextPosition fpos = focusedElement.getAbsolutePosition();

            focusedRectangle = new Rectangle(d.xToPixels(fpos.getColumn()), d
                    .yToPixels(fpos.getLine()), getCharWidth()
                    * focusedElement.getWidth(), getCharHeight()
                    * focusedElement.getHeight());
        }

        //draw focused element
        if (focusedRectangle != null) {
            g.setColor(Color.yellow);
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
        return StructuredEditorUI.HORIZONTAL_MARGIN + x * getCharWidth();
    }

    public int yToPixels(int y) {
        return StructuredEditorUI.VERTICAL_MARGIN + y * getCharHeight();
    }

    public int pixelsToX(int x) {
        return (x - StructuredEditorUI.HORIZONTAL_MARGIN) / getCharWidth();
    }

    public int pixelsToY(int y) {
        return (y - StructuredEditorUI.VERTICAL_MARGIN) / getCharHeight();
    }
}
