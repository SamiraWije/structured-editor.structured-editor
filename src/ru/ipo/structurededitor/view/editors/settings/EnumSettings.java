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
    private String selectVariantActionText = "Выбрать вариант";
    private String selectOtherVariantActionText = "Выбрать другой вариант";

    public EnumSettings() {
    }

    public String getNullText() {
        return nullText;
    }

    public String getSelectVariantActionText() {
        return selectVariantActionText;
    }

    public String getSelectOtherVariantActionText() {
        return selectOtherVariantActionText;
    }

    public EnumSettings withNullText(String nullText) {
        this.nullText = nullText;
        return this;
    }

    public EnumSettings withSelectVariantActionText(String selectVariantActionText) {
        this.selectVariantActionText = selectVariantActionText;
        return this;
    }

    public EnumSettings withSelectOtherVariantActionText(String selectOtherVariantActionText) {
        this.selectOtherVariantActionText = selectOtherVariantActionText;
        return this;
    }
}
