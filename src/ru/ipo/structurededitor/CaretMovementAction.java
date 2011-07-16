package ru.ipo.structurededitor;

import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA. User: Ilya Date: 14.01.2010 Time: 22:20:40
 */
public class CaretMovementAction extends AbstractAction {

    public static enum Direction {
        Up, Down, Left, Right,
    }

    private final Direction dir;

    /**
     * @param dir
     */
    public CaretMovementAction(Direction dir) {
        this.dir = dir;
    }

    public void actionPerformed(ActionEvent e) {
        final StructuredEditorModel editorModel =
                ((StructuredEditor) e.getSource()).getModel();

        VisibleElement rootElement = editorModel.getRootElement();
        TextPosition absolutePosition = rootElement.getAbsolutePosition();

        int rootLine = absolutePosition.getLine();
        int rootColumn = absolutePosition.getColumn();
        int rootWidth = rootElement.getWidth();
        int rootHeight = rootElement.getHeight();

        int x = editorModel.getAbsoluteCaretX();
        int y = editorModel.getAbsoluteCaretY();
        switch (dir) {
            case Down:
                if (y < rootLine + rootHeight - 1)
                    y++;
                break;
            case Up:
                if (y > rootLine)
                    y--;
                break;
            case Left:
                if (x > rootColumn)
                    x--;
                break;
            case Right:
                if (x < rootColumn + rootWidth)
                    x++;
                break;
        }

        editorModel.setAbsoluteCaretPosition(x, y);

        editorModel.repaint();
    }
}
