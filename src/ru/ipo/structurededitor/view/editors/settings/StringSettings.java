package ru.ipo.structurededitor.view.editors.settings;

import ru.ipo.structurededitor.model.EditorSettings;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 20.07.11
 * Time: 21:28
 */
public class StringSettings implements EditorSettings {

    private boolean singleLine = true;
    private boolean nullAllowed = false;
    private String emptyText = "[пусто]";
    private String nullText = "[Нет текста]";
    private String toolTipText = null;

    public StringSettings() {
    }

    public boolean isSingleLine() {
        return singleLine;
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

    public StringSettings withSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        return this;
    }

    public StringSettings withNullAllowed(boolean nullAllowed) {
        this.nullAllowed = nullAllowed;
        return this;
    }

    public StringSettings withEmptyText(String emptyText) {
        this.emptyText = emptyText;
        return this;
    }

    public StringSettings withNullText(String nullText) {
        this.nullText = nullText;
        return this;
    }

    public StringSettings withToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
        return this;
    }
}
