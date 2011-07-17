package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextProperties;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 15:28:00
 */
public class TextElement extends VisibleElement {

    private String text;

    /**
     * Влияет на способ отображения элемента. Если <code>singleLine==true</code>, то все переводы строк
     * отображаются как \n, и высота элемента всегда равно 1. Иначе переводы строк работают как переводы
     * строк, высота элемента зависит от количества строк.
     */
    private final boolean singleLine;

    private TextProperties textProperties = new TextProperties(Font.PLAIN, Color.BLACK); //TODO move to UIManager
    private TextProperties emptyTextProperties = new TextProperties(Font.BOLD, Color.GRAY);

    private String emptyText = "[Пусто]";
    private String nullText = "[null]";

    //this symbols are not drawn but the show line breaks. Number of lines = lineDelimiterSymbols.size()
    private ArrayList<Integer> lineDelimiterSymbols = new ArrayList<Integer>();

    public TextElement(StructuredEditorModel model) {
        this(model, null);
    }

    public TextElement(StructuredEditorModel model, String text) {
        this(model, text, true);
    }

    public TextElement(StructuredEditorModel model, String text, boolean singleLine) {
        super(model);

        this.singleLine = singleLine;

        setText(text);
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    protected void updateLines() {
        lineDelimiterSymbols.clear();

        int last = -1;
        lineDelimiterSymbols.add(-1);

        if (text == null) {
            lineDelimiterSymbols.add(0);
            return;
        }

        if (singleLine) {
            lineDelimiterSymbols.add(text.length());
            return;
        }

        int length = text.length();

        while (true) {
            last = text.indexOf('\n', last + 1);
            if (last == -1)
                break;

            lineDelimiterSymbols.add(last);
        }

        lineDelimiterSymbols.add(length);
    }

    public void setTextProperties(TextProperties tp) {
        textProperties = tp;
        repaint();
    }

    public TextProperties getTextProperties() {
        return textProperties;
    }

    public int getLinesCount() {
        return lineDelimiterSymbols.size() - 1;
    }

    public String getLine(int index) {
        if (text == null)
            return "";

        int lineStart = lineDelimiterSymbols.get(index) + 1;
        return text.substring(lineStart, lineDelimiterSymbols.get(index + 1));
    }

    public int getLineFirstIndex(int index) {
        return lineDelimiterSymbols.get(index) + 1;
    }

    public int getLineLastIndex(int index) {
        return lineDelimiterSymbols.get(index + 1) - 1;
    }

    private int countWidth() {
        if (getText() == null)
            return nullText.length();
        else if (getText().equals(""))
            return emptyText.length();

        int lines = getLinesCount();
        int max = 0;
        for (int i = 0; i < lines; i++) {
            int len = getLine(i).length();
            if (max < len)
                max = len;
        }

        return max;
    }

    protected int countHeight() {
        return getLinesCount();
    }

    public void drawElement(int x0, int y0, Display d) {
        String text = getText();
        if (text == null)
            d.drawString(nullText, x0, y0, emptyTextProperties);
        else if (text.equals(""))
            d.drawString(emptyText, x0, y0, emptyTextProperties);
        else {
            int lines = getLinesCount();

            for (int i = 0; i < lines; i++)
                d.drawString(getLine(i), x0, y0 + i, textProperties);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldText = this.text;
        this.text = text;

        updateLines();
        setHeight(countHeight());
        setWidth(countWidth());

        pcs.firePropertyChange("text", oldText, text);
    }

    public void setEmptyTextProperties(TextProperties emptyTextProperties) {
        this.emptyTextProperties = emptyTextProperties;
        getModel().repaint();
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
        getModel().repaint();
    }

    public void setNullText(String nullText) {
        this.nullText = nullText;
        getModel().repaint();
    }

    public TextProperties getEmptyTextProperties() {
        return emptyTextProperties;
    }

    public String getEmptyText() {
        return emptyText;
    }

    public String getNullText() {
        return nullText;
    }
}