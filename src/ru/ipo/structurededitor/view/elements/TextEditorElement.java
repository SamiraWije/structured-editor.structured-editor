package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.TextProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 21:24:49
 */
public class TextEditorElement extends TextElement {

    private int markColumn = -1;
    private int markLine = -1;

    public TextEditorElement(StructuredEditorModel model, String text, boolean singleLined) {
        super(model, text, singleLined);
        TextProperties editableTextProperties = new TextProperties(
                Font.BOLD,
                UIManager.getColor("StructuredEditor.text.edit.color")
        );
        setTextProperties(editableTextProperties);
    }

    public TextEditorElement(StructuredEditorModel model, String text) {
        this(model, text, true);
    }

    public TextEditorElement(StructuredEditorModel model) {
        this(model, null, true);
    }

    @Override
    public void drawElement(int x0, int y0, Display d) {
        if (isFocused() && ! isView()) {
            CaretData caretData = getElementCaret();
            drawSelection(x0, y0, d, caretData);
        }

        super.drawElement(x0, y0, d);
    }

    private void drawSelection(int x0, int y0, Display d, CaretData caretData) {
        //draw selection if
        // Mark is not in the position -1,-1 (this position marks the mark as absent) and
        // mark is not in the same place as the caret
        if (markColumn >= 0 && markColumn >= 0 &&
                !(markColumn == caretData.columnNormalized && markLine == caretData.line)) {

            //set selection color
            d.getGraphics().setColor(UIManager.getColor("StructuredEditor.textSelection.color"));

            if (markLine == caretData.line) {
                //if caret and mark are in one line

                int begin = Math.min(caretData.columnNormalized, markColumn);
                int end = Math.max(caretData.columnNormalized, markColumn);

                int x1 = d.xToPixels(x0 + begin), y1 = d.yToPixels(y0 + caretData.line - 1);
                int x2 = d.xToPixels(x0 + end), y2 = d.yToPixels(y0 + caretData.line);

                d.getGraphics().fillRect(x1, y2, x2 - x1, y2 - y1);
            } else {
                int xBegin, yBegin, xEnd, yEnd;
                if (markLine < caretData.line) {
                    xBegin = markColumn;
                    yBegin = markLine;
                    xEnd = caretData.columnNormalized;
                    yEnd = caretData.line;
                } else {
                    xBegin = caretData.columnNormalized;
                    yBegin = caretData.line;
                    xEnd = markColumn;
                    yEnd = markLine;
                }

                int w = getWidth();

                int x0p = d.xToPixels(x0);
                int x1p = d.xToPixels(x0 + xBegin);
                int x2p = d.xToPixels(x0 + xEnd);
                int x3p = d.xToPixels(x0 + w);

                int y1p = d.yToPixels(y0 + yBegin);
                int y2p = d.yToPixels(y0 + yBegin + 1);
                int y3p = d.yToPixels(y0 + yEnd);
                int y4p = d.yToPixels(y0 + yEnd + 1);

                d.getGraphics().fillRect(x1p, y1p, x3p - x1p, y2p - y1p);

                d.getGraphics().fillRect(x0p, y2p, x3p - x0p, y3p - y2p);

                d.getGraphics().fillRect(x0p, y3p, x2p - x0p, y4p - y3p);
            }
        }
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        CaretData caretData = getElementCaret();

        boolean isPrintable = Character.isDefined(e.getKeyChar()) && !Character.isISOControl(e.getKeyChar());
        boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
        boolean ctrl = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;
        boolean alt = (e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0;
        boolean alt_gr = (e.getModifiersEx() & KeyEvent.ALT_GRAPH_DOWN_MASK) != 0;
        boolean meta = (e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0;

        if (ctrl || alt || alt_gr || meta)
            return;

        TextPosition absolutePosition = getAbsolutePosition();
        int col0 = absolutePosition.getColumn();
        int line0 = absolutePosition.getLine();

        if (isPrintable) {
            buttonChar(caretData, e.getKeyChar());
            setAbsoluteCaretPositionTo(col0, line0, caretData);
            e.consume();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (shift)
                    return;
                if (!isSingleLine()) {
                    buttonChar(caretData, '\n');
                    setAbsoluteCaretPositionTo(col0, line0, caretData);
                    e.consume();
                }
                break;
            case KeyEvent.VK_DELETE:
                /*if (shift)
                    return;*/
                buttonDelete(caretData);
                setAbsoluteCaretPositionTo(col0, line0, caretData);
                e.consume();
                break;
            case KeyEvent.VK_BACK_SPACE:
                /*if (shift)
                    return;*/
                buttonBackSpace(caretData);
                setAbsoluteCaretPositionTo(col0, line0, caretData);
                e.consume();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_UP:
                tryStoreMark(caretData, shift);
                if (shift) {
                    tryMoveCaret(caretData, e.getKeyCode());
                    setAbsoluteCaretPositionTo(col0, line0, caretData);
                    e.consume();
                }
                break;
            case KeyEvent.VK_HOME:
                tryStoreMark(caretData, shift);
                getModel().setAbsoluteCaretPosition(col0, line0 + caretData.line);
                e.consume();
                break;
            case KeyEvent.VK_END:
                tryStoreMark(caretData, shift);
                getModel().setAbsoluteCaretPosition(
                        col0 + getLine(caretData.line).length(),
                        line0 + caretData.line
                );
                e.consume();
                break;
        }
    }

    /**
     * Moves caret.
     * @param keyCode VK_LEFT, VK_RIGHT, VK_UP, VK_BOTTOM
     */
    private void tryMoveCaret(CaretData caretData, int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                if (caretData.stringPosition > 0)
                    getCaretByPosition(caretData.stringPosition - 1, caretData);
                break;
            case KeyEvent.VK_RIGHT:
                int length = getText() == null ? 0 : getText().length();
                if (caretData.stringPosition < length)
                    getCaretByPosition(caretData.stringPosition + 1, caretData);
                break;
            case KeyEvent.VK_UP:
                if (caretData.line > 0)
                    getElementCaret(caretData.column, caretData.line - 1, caretData);
                break;
            case KeyEvent.VK_DOWN:
                if (caretData.line < getLinesCount() - 1)
                    getElementCaret(caretData.column, caretData.line + 1, caretData);
                break;
        }
    }

    private void setAbsoluteCaretPositionTo(int col0, int line0, CaretData caretData) {
        getModel().setAbsoluteCaretPosition(
                col0 + caretData.column,
                line0 + caretData.line
        );
    }

    private void tryStoreMark(CaretData caretData, boolean shift) {
        if (shift && markColumn == -1) {
            markColumn = caretData.columnNormalized;
            markLine = caretData.line;
        } else if (!shift) {
            markColumn = -1;
            markLine = -1;
        }
    }

    private void buttonChar(CaretData caretData, char c) {
        if (markColumn != -1)
            removeSelection(caretData);

        String text = getText();
        if (text == null)
            text = "";

        StringBuilder sb = new StringBuilder();

        sb.append(text.substring(0, caretData.stringPosition));
        //append whitespaces if cursor is to the right of the last line symbol, but it's not the null/empty text
        if (!text.equals(""))
            for (int i = caretData.columnNormalized; i < caretData.column; i++)
                sb.append(' ');
        sb.append(c);
        int newCaretPos = sb.length();
        sb.append(text.substring(caretData.stringPosition));

        setText(sb.toString());

        getCaretByPosition(newCaretPos, caretData);
    }

    private void buttonBackSpace(CaretData caretData) {
        if (markColumn != -1) {
            removeSelection(caretData);
        } else if (caretData.column > caretData.columnNormalized) {
            caretData.column --;
        } else if (caretData.stringPosition == 0) {
            //do nothing
        } else {
            String text = getText();
            if (text == null)
                return;

            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(0, caretData.stringPosition - 1));
            sb.append(text.substring(caretData.stringPosition));
            setText(sb.toString());

            getCaretByPosition(caretData.stringPosition - 1, caretData);
        }
    }

    private void buttonDelete(CaretData caretData) {
        if (markColumn != -1) {
            removeSelection(caretData);
        } else {
            String text = getText();
            if (text == null)
                return;

            if (caretData.stringPosition == text.length())
                return;

            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(0, caretData.stringPosition));
            //append whitespaces if needed
            for (int i = caretData.columnNormalized; i < caretData.column; i++)
                sb.append(' ');
            sb.append(text.substring(caretData.stringPosition + 1));
            setText(sb.toString());
        }
    }

    private void removeSelection(CaretData caretData) {
        if (markColumn == -1)
            return;

        String text = getText();
        if (text == null)
            return;

        CaretData markData = getElementCaret(markColumn, markLine);

        int minPosition = Math.min(markData.stringPosition, caretData.stringPosition);
        int maxPosition = Math.max(markData.stringPosition, caretData.stringPosition);

        StringBuilder sb = new StringBuilder();
        sb
                .append(text.substring(0, minPosition))
                .append(text.substring(maxPosition));

        setText(sb.toString());

        markColumn = -1;
        markLine = -1;

        getCaretByPosition(minPosition, caretData);
    }

    private CaretData getElementCaret() {
        TextPosition absolutePosition = getAbsolutePosition();
        return getElementCaret(
                getModel().getAbsoluteCaretX() - absolutePosition.getColumn(),
                getModel().getAbsoluteCaretY() - absolutePosition.getLine()
        );
    }

    private CaretData getElementCaret(int column, int line) {
        CaretData data = new CaretData();

        data.column = column;
        data.line = line;
        data.stringPosition = getLineFirstIndex(data.line) + data.column;
        data.columnNormalized = data.column;

        int lineLength = getLine(data.line).length();
        if (data.column > lineLength) {
            data.columnNormalized = lineLength;
            data.stringPosition -= data.column - data.columnNormalized;
        }

        return data;
    }

    private void getElementCaret(int column, int line, CaretData caretData) {

        caretData.column = column;
        caretData.line = line;
        caretData.stringPosition = getLineFirstIndex(caretData.line) + caretData.column;
        caretData.columnNormalized = caretData.column;

        int lineLength = getLine(caretData.line).length();
        if (caretData.column > lineLength) {
            caretData.columnNormalized = lineLength;
            caretData.stringPosition -= caretData.column - caretData.columnNormalized;
        }
    }

    private void getCaretByPosition(int position, CaretData caretData) {
        //get line for position
        int lines = getLinesCount();

        int line = lines - 1;
        for (int i = 0; i < lines; i++) {
            int firstIndex = getLineFirstIndex(i);
            if (firstIndex > position) {
                line = i - 1;
                break;
            }
        }

        caretData.line = line;
        caretData.column = position - getLineFirstIndex(line);
        caretData.stringPosition = position;
        caretData.columnNormalized = caretData.column;
    }

    private static class CaretData {

        /**
         * строка с курсором
         */
        public int line;
        /**
         * колонка с курсором
         */
        public int column;
        /**
         * Положение в строке. Курсор стоит перед символом с этим индексом
         */
        public int stringPosition;
        /**
         * Колонка, которая совпадает с column, если курсор внутри строки, или совпадает с самым правым
         * возможным положением внутри строки, если курсор правее. Ну трудно объяснить, да
         */
        public int columnNormalized;
    }

}