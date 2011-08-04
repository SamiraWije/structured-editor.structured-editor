package ru.ipo.structurededitor.view.editors.settings;

import ru.ipo.structurededitor.model.EditorSettings;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 28.07.11
 * Time: 17:43
 */
public class NumberSettings implements EditorSettings {

    private String emptyText = "[Введите число]";

    public String getEmptyText() {
        return emptyText;
    }

    public NumberSettings withEmptyText(String emptyText) {
        this.emptyText = emptyText;
        return this;
    }
}
