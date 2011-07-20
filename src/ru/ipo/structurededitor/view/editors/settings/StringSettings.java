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

    public StringSettings() {
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public StringSettings withSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        return this;
    }
}
