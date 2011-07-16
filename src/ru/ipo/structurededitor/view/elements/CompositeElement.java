package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.TextProperties;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 15:58:23
 */
public class CompositeElement extends VisibleElement {

    private static class PositionedElement {

        public int x;
        public int y;
        public VisibleElement element;

        public PositionedElement(int x, int y, VisibleElement element) {
            this.x = x;
            this.y = y;
            this.element = element;
        }

        public PositionedElement(VisibleElement element) {
            this.element = element;
        }
    }

    private final ArrayList<PositionedElement> elements = new ArrayList<PositionedElement>();
    private Orientation orientation;
    private char spaceChar = 0;
    public final Color SPACE_CHAR_COLOR = Color.BLACK;
    //private int previousOutDirection = Integer.MAX_VALUE;

    public enum Orientation {
        Vertical,
        Horizontal,
    }

    private final PropertyChangeListener sizeChangeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            reposition();
        }
    };

    public CompositeElement(StructuredEditorModel model, Orientation orientation) {
        super(model);
        this.orientation = orientation;
        reposition();
    }

    public CompositeElement(StructuredEditorModel model, Orientation orientation, char spaceChar) {
        super(model);
        this.orientation = orientation;
        this.spaceChar = spaceChar;
        reposition();
    }

    public void setElements(VisibleElement... visibleElementList) {
        setElements(Arrays.asList(visibleElementList));
    }

    public void setElements(List<VisibleElement> visibleElementList) {
        //remove all listeners from current elements
        for (PositionedElement element : elements) {
            element.element.removePropertyChangeListener("width", sizeChangeListener);
            element.element.removePropertyChangeListener("height", sizeChangeListener);
            element.element.setParent(null);
        }

        elements.clear();

        for (VisibleElement visibleElement : visibleElementList) {
            elements.add(new PositionedElement(visibleElement));
            visibleElement.setParent(this);

            visibleElement.addPropertyChangeListener("width", sizeChangeListener);
            visibleElement.addPropertyChangeListener("height", sizeChangeListener);
        }

        reposition();

        getModel().testFocus();
    }

    public void add(VisibleElement element) {
        add(element, elements.size());
    }

    public void add(VisibleElement element, int index) {
        elements.add(index, new PositionedElement(element));
        element.setParent(this);
        reposition();

        element.addPropertyChangeListener("width", sizeChangeListener);
        element.addPropertyChangeListener("height", sizeChangeListener);
    }

    private void reposition() {
        if (elements.size() == 0) return;

        elements.get(0).x = 0;
        elements.get(0).y = 0;

        for (int i = 1; i < elements.size(); i++) {
            switch (orientation) {
                case Horizontal:
                    elements.get(i).x = elements.get(i - 1).x + elements.get(i - 1).element.getWidth() + 1;
                    if (spaceChar != 0)
                        elements.get(i).x += 2;
                    elements.get(i).y = elements.get(i - 1).y;
                    break;
                case Vertical:
                    elements.get(i).x = elements.get(i - 1).x;
                    /*if (spaceChar != 0)
                        elements.get(i).x += 2;*/
                    elements.get(i).y = elements.get(i - 1).y + elements.get(i - 1).element.getHeight();
                    break;
            }
        }

        setWidth(countWidth());
        setHeight(countHeight());
    }

    public void remove(VisibleElement element) {
        for (int i = 0; i < elements.size(); i++)
            if (elements.get(i).element == element) {
                remove(i);
                return;
            }
    }

    public void remove(int index) {
        PositionedElement removed = elements.remove(index);
        removed.element.removePropertyChangeListener("width", sizeChangeListener);
        removed.element.removePropertyChangeListener("height", sizeChangeListener);
        removed.element.setParent(null);
        reposition();

        getModel().testFocus();
    }

    public VisibleElement get(int index) {
        return elements.get(index).element;
    }

    public void drawElement(int x0, int y0, Display d) {

        for (int i = 0; i < elements.size(); i++) {
            PositionedElement pe = elements.get(i);
            pe.element.drawElement(x0 + pe.x, y0 + pe.y, d);
            if (spaceChar != 0 && i < elements.size() - 1)
                d.drawString(String.valueOf(spaceChar), x0 + pe.x + pe.element.getWidth() + 1, y0 + pe.y,
                        new TextProperties(Font.PLAIN, SPACE_CHAR_COLOR));
        }
    }

    public int countWidth() {
        switch (orientation) {
            case Horizontal:
                PositionedElement lastH = elements.get(elements.size() - 1);
                return lastH.x + lastH.element.getWidth();
            case Vertical:
                int maxWidth = 0;
                for (PositionedElement pe : elements) {
                    int w = pe.element.getWidth();
                    if (w > maxWidth)
                        maxWidth = w;
                }
                return maxWidth + 1;
        }

        return -1; //may not occur
    }

    public int countHeight() {
        switch (orientation) {
            case Horizontal:
                int maxHeight = 0;
                for (PositionedElement pe : elements) {
                    int h = pe.element.getHeight();
                    if (h > maxHeight)
                        maxHeight = h;
                }
                return maxHeight;
            case Vertical:
                PositionedElement lastV = elements.get(elements.size() - 1);
                return lastV.y + lastV.element.getHeight();
        }

        return -1; //may not occur
    }

    @Override
    public int getChildrenCount() {
        return elements.size();
    }

    @Override
    public VisibleElement getChild(int index) {
        return elements.get(index).element;
    }

    @Override
    public TextPosition getChildPosition(int index) {
        PositionedElement element = elements.get(index);
        return new TextPosition(element.y, element.x);
    }

}
