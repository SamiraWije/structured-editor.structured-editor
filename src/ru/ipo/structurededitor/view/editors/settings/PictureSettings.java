package ru.ipo.structurededitor.view.editors.settings;

import ru.ipo.structurededitor.model.EditorSettings;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 20.07.11
 * Time: 21:28
 */
public class PictureSettings implements EditorSettings {

    Dimension dimension=new Dimension(3,3);
    private boolean nullAllowed = false;
    private String emptyText = "[пусто]";
    private String nullText = "[Нет текста]";
    private String toolTipText = null;

    public PictureSettings() {
    }

    public Dimension getDimension() {
        return dimension;
    }

    public boolean isNullAllowed() {
        return nullAllowed;
    }

    public String getEmptyText() {
        return emptyText;
    }

    public String getNullText() {
        return nullText;
    }

    public String getToolTipText() {
        return toolTipText;
    }


    public PictureSettings withNullAllowed(boolean nullAllowed) {
        this.nullAllowed = nullAllowed;
        return this;
    }

    public PictureSettings withEmptyText(String emptyText) {
        this.emptyText = emptyText;
        return this;
    }

    public PictureSettings withNullText(String nullText) {
        this.nullText = nullText;
        return this;
    }

    public PictureSettings withToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
        return this;
    }
    public PictureSettings withDimension(Dimension dimension) {
        this.dimension=dimension;
        return this;
    }
}
