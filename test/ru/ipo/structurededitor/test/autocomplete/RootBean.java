package ru.ipo.structurededitor.test.autocomplete;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 2:37
 */
public class RootBean implements DSLBean {

    private String stringValue;
    private int intValue;
    private AbstractBean abstractValue;
    private EnumExample enumExample;

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public AbstractBean getAbstractValue() {
        return abstractValue;
    }

    public void setAbstractValue(AbstractBean abstractValue) {
        this.abstractValue = abstractValue;
    }

    public EnumExample getEnumExample() {
        return enumExample;
    }

    public void setEnumExample(EnumExample enumExample) {
        this.enumExample = enumExample;
    }

    @Override
    public Cell getLayout() {
        return new Vert(
                new Horiz(new ConstantCell("String ="), new FieldCell("stringValue", new StringSettings().withSingleLine(false))),
                new Horiz(new ConstantCell("Int ="), new FieldCell("intValue")),
                new Horiz(new ConstantCell("Abstract ="), new FieldCell("abstractValue")),
                new Horiz(new ConstantCell("EnumEx ="), new FieldCell("enumExample"))
        );
    }
}
