package ru.ipo.structurededitor.model;

/**
 * Ячейка с картинкой из файла
 */
public class PictureCell implements Cell {

    private String fileNameField;
    private EditorSettings settings;

    public PictureCell(String fileNameField) {
        this.fileNameField = fileNameField;
    }

    public PictureCell(String fileNameField, EditorSettings settings) {
        this.fileNameField = fileNameField;
        this.settings = settings;
    }

    public String getFileNameField() {
        return fileNameField;
    }
   public EditorSettings getSettings() {
        return settings;
    }
}
