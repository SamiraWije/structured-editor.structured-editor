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

    private String clearArrayActionText = "Очистить массив";
    private String insertActionText = "Вставить элемент массива";
    private String removeActionText = "Удалить элемент массива";
    private String createArrayActionText = "Создать массив";

    private boolean allowClearFilledArray = false;
    private int minElements = 0;
    private int maxElements = Integer.MAX_VALUE;

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

    public String getClearArrayActionText() {
        return clearArrayActionText;
    }

    public String getInsertActionText() {
        return insertActionText;
    }

    public String getRemoveActionText() {
        return removeActionText;
    }

    public int getMinElements() {
        return minElements;
    }

    public int getMaxElements() {
        return maxElements;
    }

    public boolean isAllowClearFilledArray() {
        return allowClearFilledArray;
    }

    public String getCreateArrayActionText() {
        return createArrayActionText;
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

    public ArraySettings withClearArrayActionText(String removeAllActionText) {
        this.clearArrayActionText = removeAllActionText;
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

    public ArraySettings withMaxElements(int maxElements) {
        this.maxElements = maxElements;
        return this;
    }

    public ArraySettings withMinElements(int minElements) {
        this.minElements = minElements;
        return this;
    }

    public ArraySettings withAllowClearFilledArray(boolean allowClearFilledArray) {
        this.allowClearFilledArray = allowClearFilledArray;
        return this;
    }

    public ArraySettings withCreateArrayActionText(String createArrayActionText) {
        this.createArrayActionText = createArrayActionText;
        return this;
    }
}
