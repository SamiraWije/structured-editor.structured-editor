package ru.ipo.structurededitor.testLang.testLang;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.ConstantCell;
import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Поле состоит из одного нередактируемого поля
 */
@DSLBeanParams(shortcut = "bean a 1", description = "Первый бин")
public class BeanA1 extends BeanA {
    public Cell getLayout() {
        return new ConstantCell("asdf");
    }
}
