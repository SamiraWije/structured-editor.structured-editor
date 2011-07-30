package ru.ipo.structurededitor.testLang.testLang;

import ru.ipo.structurededitor.model.*;

/**
 * Пример сложного поля из нескольких ячеек
 */
@DSLBeanParams(shortcut = "bean a 2", description = "Второй бин")
public class BeanA2 extends BeanA {

    private String x;

    public Cell getLayout() {
        return new Horiz(new Vert(new ConstantCell("BEAN 2"), new ConstantCell(
                "strFld ="), new ConstantCell("x =")), new Vert(new ConstantCell(" "),
                new FieldCell("strFld"), new FieldCell("x")));
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }
}
