package ru.ipo.structurededitor.example.testLang;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.ConstantCell;

/**
 * Поле состоит из одного нередактируемого поля
 */
public class BeanA1 extends BeanA {
    public Cell getLayout() {
        return new ConstantCell("asdf");
    }
}
