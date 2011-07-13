package ru.ipo.structurededitor.test.autocomplete;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 2:52
 */
public class ExtensionBean4 extends AbstractExtensionBean {

    private int x;

    private EnumExample[] enums;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public EnumExample[] getEnums() {
        return enums;
    }

    public void setEnums(EnumExample[] enums) {
        this.enums = enums;
    }

    @Override
    public Cell getLayout() {
        return new Vert(
                new Horiz(new ConstantCell("bean 4 ="), new FieldCell("x")),
                new Horiz(new ConstantCell("enums ="), new VertArray("enums"))
        );
    }
}
