package ru.ipo.structurededitor.actions;

import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 11.07.11
 * Time: 0:46
 */
public class ActionsListComponent extends JList implements MouseMotionListener {

    private final ListCellRenderer actionsCellRenderer = new ActionsCellRenderer(this);

    private DefaultListModel model = new DefaultListModel();
    private HashMap<KeyStroke, VisibleElementAction> stroke2action = new HashMap<KeyStroke, VisibleElementAction>();

    private static final VisibleElementAction prototypeAction = new VisibleElementAction("", KeyStroke.getKeyStroke("A")) {
        @Override
        public void run(StructuredEditorModel model) {
            //do nothing
        }
    };

    public ActionsListComponent() {
        setModel(model);
        setCellRenderer(actionsCellRenderer);
        setBackground(UIManager.getColor("ActionsListComponent.background"));
        setFocusable(false);
        setPrototypeCellValue(prototypeAction);
        setVisibleRowCount(6);
        addMouseMotionListener(this);
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

    @Override
    public void mouseDragged(MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
