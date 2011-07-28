package ru.ipo.structurededitor.view.editors.settings;

import ru.ipo.structurededitor.model.EditorSettings;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 20.07.11
 * Time: 14:49
 */
public class AbstractDSLBeanSettings implements EditorSettings {

    /**
     * Текст в редакторе
     */
    private String nullValueText = "[Ничего не выбрано]";
    /**
     * Текст действия для установки null
     */
    private String setNullActionText = "Удалить объект";
    /**
     * Текст действия по выбору вариантов
     */
    private String selectVariantActionText = "Выбрать вариант";

    public AbstractDSLBeanSettings() {
    }

    public String getNullValueText() {
        return nullValueText;
    }

    public String getSelectVariantActionText() {
        return selectVariantActionText;
    }

    public String getSetNullActionText() {
        return setNullActionText;
    }

    public AbstractDSLBeanSettings withNullValueText(String nullValueText) {
        this.nullValueText = nullValueText;
        return this;
    }

    public AbstractDSLBeanSettings withSelectVariantActionText(String selectVariantActionText) {
        this.selectVariantActionText = selectVariantActionText;
        return this;
    }

    public AbstractDSLBeanSettings withSetNullActionText(String setNullActionText) {
        this.setNullActionText = setNullActionText;
        return this;
    }
}
