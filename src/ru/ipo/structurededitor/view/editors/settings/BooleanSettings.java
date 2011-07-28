package ru.ipo.structurededitor.view.editors.settings;

import ru.ipo.structurededitor.model.EditorSettings;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 28.07.11
 * Time: 17:43
 */
public class BooleanSettings implements EditorSettings {

    private String trueText = "да";
    private String falseText = "нет";
    private String changeActionText = "Изменить";

    public String getTrueText() {
        return trueText;
    }

    public String getFalseText() {
        return falseText;
    }

    public String getChangeActionText() {
        return changeActionText;
    }

    public BooleanSettings withTrueText(String trueText) {
        this.trueText = trueText;
        return this;
    }

    public BooleanSettings withFalseText(String falseText) {
        this.falseText = falseText;
        return this;
    }

    public BooleanSettings withChangeActionText(String changeActionText) {
        this.changeActionText = changeActionText;
        return this;
    }
}
