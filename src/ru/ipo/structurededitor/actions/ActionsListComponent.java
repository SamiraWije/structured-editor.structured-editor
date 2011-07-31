package ru.ipo.structurededitor.actions;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private ActionsListModel model = new ActionsListModel();
    private HashMap<KeyStroke, VisibleElementAction> stroke2action = new HashMap<KeyStroke, VisibleElementAction>();
    private int highlightIndex = -1;

    private int maxVisibleActions = 6;
    private boolean constantVisibleActions = true;

    private static final VisibleElementAction prototypeAction = new VisibleElementAction("", "add.png", "A") {
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
        setFixedCellWidth(-1);
        setVisibleRowCount(maxVisibleActions);
        //TODO don't allow select "no actions available"
        addMouseListener(this);
        addMouseMotionListener(this);
        //TODO make mouse wheel work well
//        addMouseWheelListener(this);

        //don't allow selection of elements
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                setSelectedIndices(new int[0]);
            }
        });
    }

    public int getHighlightIndex() {
        return highlightIndex;
    }

    public void addAction(VisibleElementAction action) {
        VisibleElementAction concurrentAction = stroke2action.get(action.getKeyStroke());
        if (concurrentAction == null)
            stroke2action.put(action.getKeyStroke(), action);

        model.addElement(action);

        resizeByActions();
    }

    public void removeAction(VisibleElementAction action) {
        stroke2action.remove(action.getKeyStroke());

        model.removeElement(action);

        resizeByActions();
    }

    public void clearActions() {
        stroke2action.clear();

        model.clear();

        resizeByActions();
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

        //don't select item if mouse is not exactly over it.
        //Otherwise an empty space after the last item always selects the last item
        if (highlightIndex != -1 && !getCellBounds(highlightIndex, highlightIndex).contains(point))
            highlightIndex = -1;

        //don't select element that shows that no actions are available
        if (highlightIndex != -1 && model.getElementAt(highlightIndex) == null)
            highlightIndex = -1;

        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //mouseMoved(e);
    }

    public int getMaxVisibleActions() {
        return maxVisibleActions;
    }

    public void setMaxVisibleActions(int maxVisibleActions) {
        this.maxVisibleActions = maxVisibleActions;
        resizeByActions();
    }

    public boolean isConstantVisibleActions() {
        return constantVisibleActions;
    }

    public void setConstantVisibleActions(boolean constantVisibleActions) {
        this.constantVisibleActions = constantVisibleActions;
        resizeByActions();
    }

    public boolean hasAvailableActions() {
        return model.hasAvailableActions();
    }

    private void resizeByActions() {
        int size;

        if (constantVisibleActions)
            size = maxVisibleActions;
        else
            size = Math.min(model.getSize(), maxVisibleActions);

        setVisibleRowCount(size);
    }
}
