package ru.ipo.structurededitor.view.editors.settings;

import ru.ipo.structurededitor.model.EditorSettings;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 28.07.11
 * Time: 21:57
 */
public class ArraySettings implements EditorSettings {

    private boolean nullAllowed = false;
    private String nullText = "[нет данных]";
    private String zeroElementsText = "[нет элементов]";
    private String removeAllActionText = "Удалить массив";
    private String insertActionText = "Вставить элемент массива";
    private String removeActionText = "Удалить элемент массива" +
            "";

    public ArraySettings() {
    }

    public boolean isNullAllowed() {
        return nullAllowed;
    }

    public String getNullText() {
        return nullText;
    }

    public String getZeroElementsText() {
        return zeroElementsText;
    }

    public String getRemoveAllActionText() {
        return removeAllActionText;
    }

    public String getInsertActionText() {
        return insertActionText;
    }

    public String getRemoveActionText() {
        return removeActionText;
    }

    public ArraySettings withNullAllowed(boolean nullAllowed) {
        this.nullAllowed = nullAllowed;
        return this;
    }

    public ArraySettings withNullText(String nullText) {
        this.nullText = nullText;
        return this;
    }

    public ArraySettings withZeroElementsText(String zeroElementsText) {
        this.zeroElementsText = zeroElementsText;
        return this;
    }

    public ArraySettings withRemoveAllActionText(String removeAllActionText) {
        this.removeAllActionText = removeAllActionText;
        return this;
    }

    public ArraySettings withInsertActionText(String insertActionText) {
        this.insertActionText = insertActionText;
        return this;
    }

    public ArraySettings withRemoveActionText(String removeActionText) {
        this.removeActionText = removeActionText;
        return this;
    }
}
