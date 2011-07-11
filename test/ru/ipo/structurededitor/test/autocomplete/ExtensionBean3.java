package ru.ipo.structurededitor.test.autocomplete;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.ConstantCell;
import ru.ipo.structurededitor.model.FieldCell;
import ru.ipo.structurededitor.model.Horiz;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 2:52
 */
public class ExtensionBean3 extends AbstractExtensionBean {

    private int x;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public Cell getLayout() {
        return new Horiz(new ConstantCell("bean 3 ="), new FieldCell("x"));
    }
}
