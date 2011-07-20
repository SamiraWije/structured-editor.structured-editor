package ru.ipo.structurededitor.view.editors.settings;

import ru.ipo.structurededitor.model.EditorSettings;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 20.07.11
 * Time: 13:48
 *
 * Настройки DSLBean, для неабстрактных бинов
 */
public class DSLBeanSettings implements EditorSettings {

    /**
     * Может ли значение быть null
     */
    private boolean nullAllowed = false;

    /**
     * Видимый текст, если значение null
     */
    private String nullText = "null (Enter для содания)";

    /**
     * Текст действия для установки null
     */
    private String setNullActionText = "Удалить объект";

    /**
     * Текст действия создания объекта
     */
    private String createBeanActionText = "Создать объект";

    public DSLBeanSettings() {
    }

    public boolean isNullAllowed() {
        return nullAllowed;
    }

    public String getNullText() {
        return nullText;
    }

    public String getSetNullActionText() {
        return setNullActionText;
    }

    public String getCreateBeanActionText() {
        return createBeanActionText;
    }

    public void withNullAllowed(boolean nullAllowed) {
        this.nullAllowed = nullAllowed;
    }

    public void withNullText(String nullText) {
        this.nullText = nullText;
    }

    public void withSetNullActionText(String setNullActionText) {
        this.setNullActionText = setNullActionText;
    }

    public void withCreateBeanActionText(String createBeanActionText) {
        this.createBeanActionText = createBeanActionText;
    }
}
