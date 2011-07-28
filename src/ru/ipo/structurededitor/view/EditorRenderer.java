package ru.ipo.structurededitor.view;

import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.ArrayEditor;
import ru.ipo.structurededitor.view.editors.FieldEditor;
import ru.ipo.structurededitor.view.elements.CompositeElement;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

/**
 * Класс, генерирующий графические элементы
 */
public class EditorRenderer {

    private final EditorsRegistry reg;
    private final VisibleElement renderResult;
    private final StructuredEditorModel model;

    /**
     * Конструктор панели для редактирования всего JavaBean в целом Не отдельной
     * ячейки, а всего целиком!
     *
     * @param model
     * @param editableBean
     */
    @SuppressWarnings({"JavaDoc"})
    public EditorRenderer(StructuredEditorModel model, DSLBean editableBean) {
        this.model = model;
        reg = model.getEditorsRegistry();


        Cell layout;
        if (model.isView() && editableBean instanceof DSLBeanView)
            layout = ((DSLBeanView) editableBean).getViewLayout();
        else
            layout = editableBean.getLayout();
        renderResult = render(layout, editableBean);
    }

    public VisibleElement getRenderResult() {
        return renderResult;
    }

    private VisibleElement render(Cell layout, DSLBean editableBean) {

        if (layout instanceof ConstantCell) {
            ConstantCell cell = (ConstantCell) layout;
            return new TextElement(model, cell.getText());
        } else if (layout instanceof FieldCell) {
            FieldCell fieldCell = (FieldCell) layout;
            FieldEditor ed = reg.getEditor(
                    editableBean.getClass(),
                    fieldCell.getFieldName(),
                    editableBean,
                    null,
                    model,
                    fieldCell.getSettings()
            );
            return ed.getElement();
        }
        if (layout instanceof Vert || layout instanceof Horiz) {
            final Cell[] cells;
            if (layout instanceof Vert)
                cells = ((Vert) layout).getCells();
            else
                cells = ((Horiz) layout).getCells();

            CompositeElement res = new CompositeElement(model,
                    layout instanceof Vert ? CompositeElement.Orientation.Vertical
                            : CompositeElement.Orientation.Horizontal);

            for (Cell cell : cells)
                res.add(render(cell, editableBean));

            return res;
        } else if (layout instanceof ArrayFieldCell) {
            ArrayFieldCell arrayFieldCell = (ArrayFieldCell) layout;
            ArrayFieldCell.Orientation orientation = arrayFieldCell.getOrientation();

            ArrayEditor ed = new ArrayEditor(
                    editableBean,
                    arrayFieldCell.getFieldName(),
                    null,
                    orientation == ArrayFieldCell.Orientation.Vertical
                            ? CompositeElement.Orientation.Vertical :
                            CompositeElement.Orientation.Horizontal,
                    arrayFieldCell.getSpaceChar(),
                    model,
                    arrayFieldCell.getArraySettings(),
                    arrayFieldCell.getItemsSettings()
            );
            return ed.getElement();

        }

        throw new Error("Surprise: unknown layout in EditorRenderer.render()");
    }
}