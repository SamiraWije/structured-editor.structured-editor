package ru.ipo.structurededitor.testLang.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
public abstract class ArrayExpr extends Expr {
    private Expr items[];
    protected char op;
    protected boolean vert = false;

    public Expr[] getItems() {
        return items;
    }

    public void setItems(Expr items[]) {
        this.items = items;
    }

    public Cell getLayout() {
        if (vert)
            return new ArrayFieldCell("items", ArrayFieldCell.Orientation.Vertical);
        else
            return new Horiz(new ConstantCell("("), new ArrayFieldCell("items", ArrayFieldCell.Orientation.Horizontal), new ConstantCell(")"));
    }
}