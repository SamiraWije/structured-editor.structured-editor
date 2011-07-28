package ru.ipo.structurededitor.view.editors.settings;

import ru.ipo.structurededitor.model.EditorSettings;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 28.07.11
 * Time: 21:28
 */
public class EnumSettings implements EditorSettings {

    private String nullText = "[Ничего не выбрано]";

    public EnumSettings() {
    }

    public String getNullText() {
        return nullText;
    }

    public EnumSettings withNullText(String nullText) {
        this.nullText = nullText;
        return this;
    }
}
