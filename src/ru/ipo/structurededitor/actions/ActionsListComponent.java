package ru.ipo.structurededitor.actions;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 11.07.11
 * Time: 0:46
 */
public class ActionsListComponent extends JList implements MouseListener, MouseMotionListener, MouseWheelListener {

    private StructuredEditor editor;
    private DefaultListModel model = new DefaultListModel();
    private HashMap<KeyStroke, VisibleElementAction> stroke2action = new HashMap<KeyStroke, VisibleElementAction>();
    private int highlightIndex = -1;

    private static final VisibleElementAction prototypeAction = new VisibleElementAction("", "add.png", KeyStroke.getKeyStroke("A")) {
        @Override
        public void run(StructuredEditorModel model) {
            //do nothing, this action is needed for component to evaluate its size
        }
    };

    public ActionsListComponent(StructuredEditor editor) {
        this.editor = editor;
        setModel(model);
        ListCellRenderer actionsCellRenderer = new ActionsCellRenderer(this);
        setCellRenderer(actionsCellRenderer);
        setBackground(UIManager.getColor("ActionsListComponent.background"));
        setFocusable(false);
        setPrototypeCellValue(prototypeAction);
        setVisibleRowCount(6);
        addMouseListener(this);
        addMouseMotionListener(this);
        //TODO make mouse wheel work well
//        addMouseWheelListener(this);
    }

    public int getHighlightIndex() {
        return highlightIndex;
    }

    public void addAction(VisibleElementAction action) {
        model.addElement(action);

        VisibleElementAction concurrentAction = stroke2action.get(action.getKeyStroke());
        if (concurrentAction == null)
            stroke2action.put(action.getKeyStroke(), action);
    }

    public void removeAction(VisibleElementAction action) {
        model.removeElement(action);
        stroke2action.remove(action.getKeyStroke());
    }

    public void clearActions() {
        model.clear();
        stroke2action.clear();
    }

    public VisibleElementAction getSelectedAction() {
        return (VisibleElementAction) getSelectedValue();
    }

    public boolean hasKeyStroke(VisibleElementAction action) {
        return action == stroke2action.get(action.getKeyStroke());
    }

    public VisibleElementAction getActionByKeyEvent(KeyEvent keyEvent) {
        KeyStroke strokeForEvent = KeyStroke.getKeyStrokeForEvent(keyEvent);
        return stroke2action.get(strokeForEvent);
    }

    //mouse and mouse motion listeners
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (highlightIndex == -1)
            return;

        VisibleElementAction action = (VisibleElementAction) getModel().getElementAt(highlightIndex);
        if (action != null)
            action.run(editor.getModel());
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {
        highlightIndex = -1;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        Point point = e.getPoint();

        highlightIndex = locationToIndex(point);

        if (highlightIndex != -1 && !getCellBounds(highlightIndex, highlightIndex).contains(point))
            highlightIndex = -1;

        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //mouseMoved(e);
    }
}