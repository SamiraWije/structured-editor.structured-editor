package ru.ipo.structurededitor.model;

/**
 * User: Ilya
 * Ячейка с редактором для поля JavaBean
 */
public class FieldCell implements Cell {
    /**
     * имя поля в JavaBean для редактирования
     */
    private String fieldName;
    private EditorSettings settings;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    private boolean singleLined = false;

    public boolean getSingleLined() {
        return singleLined;
    }

    public void setSingleLined(boolean singleLined) {
        this.singleLined = singleLined;
    }

    public FieldCell(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldCell(String fieldName, EditorSettings settings) {
        this.fieldName = fieldName;
        this.settings = settings;
    }

    public String getFieldName() {
        return fieldName;
    }

    public EditorSettings getSettings() {
        return settings;
    }
}
