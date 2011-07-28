package ru.ipo.structurededitor.model;

/**
 * Ячейка с константным текстом
 */
public class ConstantCell implements Cell {

    private String text;
    private ConstCellSettings settings;

    public ConstantCell(String text) {
        this.text = text;
        this.settings = new ConstCellSettings();
    }

    public ConstantCell(String text, ConstCellSettings settings) {
        this.text = text;
        this.settings = settings;
    }

    public String getText() {
        return text;
    }

    public ConstCellSettings getSettings() {
        return settings;
    }
}
