package ru.ipo.structurededitor.model;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 29.07.11
 * Time: 1:10
 */
public class PictureCellSettings {

    private String toolTipText = null;

    public PictureCellSettings() {
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public PictureCellSettings withToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
        return this;
    }
}
