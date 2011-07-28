package ru.ipo.structurededitor.model;

import ru.ipo.structurededitor.view.TextProperties;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 29.07.11
 * Time: 1:10
 */
public class ConstCellSettings {

    private String toolTipText = null;
    private TextProperties textProperties = null; //null means default text element properties

    public ConstCellSettings() {
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public TextProperties getTextProperties() {
        return textProperties;
    }

    public ConstCellSettings withToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
        return this;
    }

    public ConstCellSettings withTextProperties(TextProperties textProperties) {
        this.textProperties = textProperties;
        return this;
    }
}
