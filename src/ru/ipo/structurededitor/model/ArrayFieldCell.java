package ru.ipo.structurededitor.model;

/**
 * User: Ilya
 * Ячейка с редактором для поля JavaBean типа массив с вертик. расположением элементов
 */
public class ArrayFieldCell implements Cell {

    public static enum Orientation {
        Vertical,
        Horizontal
    }

    /**
     * имя поля в JavaBean для редактирования
     */
    private final String fieldName;
    private final Orientation orientation;

    private char spaceChar = 0;
    private EditorSettings arraySettings;
    private EditorSettings itemsSettings;

    public ArrayFieldCell(String fieldName, Orientation orientation) {
        this.fieldName = fieldName;
        this.orientation = orientation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public char getSpaceChar() {
        return spaceChar;
    }

    public EditorSettings getArraySettings() {
        return arraySettings;
    }

    public EditorSettings getItemsSettings() {
        return itemsSettings;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public ArrayFieldCell withArraySettings(EditorSettings arraySettings) {
        this.arraySettings = arraySettings;
        return this;
    }

    public ArrayFieldCell withItemsSettings(EditorSettings itemsSettings) {
        this.itemsSettings = itemsSettings;
        return this;
    }

    public ArrayFieldCell withSpaceChar(char spaceChar) {
        this.spaceChar = spaceChar;
        return this;
    }
}