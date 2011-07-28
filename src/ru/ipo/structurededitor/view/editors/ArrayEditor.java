package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.ArrayFieldMask;
import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.controller.MaskComposition;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.CompositeElement;
import ru.ipo.structurededitor.view.elements.ContainerElement;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.07.2010
 * Time: 13:17:50
 */
public class ArrayEditor extends FieldEditor {

    //implementation note: here in this editor values in array are always parallel to elements in composite element

    //TODO add actions to swap array elements, to insert, backspace

    private EditorSettings itemsSettings;

    private final VisibleElementAction setNullValueAction = new VisibleElementAction("Удалить массив", "delete.png", KeyStroke.getKeyStroke("control DELETE")) { //TODO set normal text
        @Override
        public void run(StructuredEditorModel model) {
            setValue(null);
            updateElement();

            getModel().moveCaretToElement(getElement());
        }
    };

    public ArrayEditor(Object o, String fieldName, FieldMask mask, CompositeElement.Orientation orientation, char spaceChar, final StructuredEditorModel model, EditorSettings settings, EditorSettings itemsSettings) {
        super(o, fieldName, mask, model, settings);
        this.itemsSettings = itemsSettings;

        final CompositeElement arrayElement = new CompositeElement(model, orientation, spaceChar);

        setElement(arrayElement);

        updateElement();
    }

    /*private void addAddAction(VisibleElement arrayElement) {
        VisibleElementAction addArrayElementAction = new VisibleElementAction("Добавить элемент массива", "add.png", KeyStroke.getKeyStroke("ENTER")) { //TODO add normal text
            @Override
            public void run(StructuredEditorModel model) {
                addElementToArray(-1);

                int selectIndex = getElement().getChildrenCount() - 1;
                getModel().moveCaretToElement(getElement().getChild(selectIndex));
            }
        };

        arrayElement.addAction(addArrayElementAction);
    }*/

    @SuppressWarnings({"SuspiciousSystemArraycopy"})
    private void addElementToArray(int newIndex) {
        Class<?> fieldType = getFieldType();
        Class<?> componentType = fieldType.getComponentType();

        //get value
        Object value = getValue();
        if (value == null)
            value = Array.newInstance(componentType, 0);

        //add element to the array
        int length = Array.getLength(value);
        Object newValue = Array.newInstance(componentType, 1 + length);

//        if (newIndex < 0)
//            newIndex += 1 + length;

        System.arraycopy(value, 0, newValue, 0, newIndex);
        System.arraycopy(value, newIndex, newValue, newIndex + 1, length - newIndex);

        setValue(newValue);
        updateElement();
    }

    private FieldEditor createEditorInstance(int index) {
        final EditorsRegistry reg = getModel().getEditorsRegistry();

        return reg.getEditor(
                (Class<? extends DSLBean>) getObject().getClass(),
                getFieldName(),
                getObject(),
                MaskComposition.composeMasks(new ArrayFieldMask(index), getMask()),
                getModel(),
                itemsSettings
        );
    }

    private VisibleElement createNullElement() {
        TextElement nullElement = new TextElement(getModel(), "[Нет массива]"); //TODO set normal text

        nullElement.addAction(new InsertArrayElementAction(0));

        return nullElement;
    }

    private VisibleElement createZeroElement() {
        TextElement zeroElement = new TextElement(getModel(), "[Нет элементов массива]"); //TODO set normal text

        zeroElement.addAction(new InsertArrayElementAction(0));
        zeroElement.addAction(setNullValueAction);

        return zeroElement;
    }

    @Override
    protected void updateElement() {
        Object value = getValue();

        CompositeElement root = (CompositeElement) getElement();

        if (value == null) {
            root.setElements(createNullElement());
            return;
        }

        int length = Array.getLength(value);

        if (length == 0) {
            root.setElements(createZeroElement());
            return;
        }

        ArrayList<VisibleElement> elements = new ArrayList<VisibleElement>(length);

        for (int ind = 0; ind < length; ind++) {
            FieldEditor editor = createEditorInstance(ind);
            VisibleElement element = editor.getElement();
            ContainerElement container = new ContainerElement(getModel(), element);

            container.addAction(new DeleteArrayElementAction(ind));
            container.addAction(new InsertArrayElementAction(ind + 1));

            elements.add(container);
        }

        root.setElements(elements);
    }

    private class InsertArrayElementAction extends VisibleElementAction {

        private int index;

        public InsertArrayElementAction(int index) {
            super("Вставить элемент массива", "add.png", KeyStroke.getKeyStroke("ENTER")); //TODO set normal text
            this.index = index;
        }

        @Override
        public void run(StructuredEditorModel model) {
            addElementToArray(index);

            getModel().moveCaretToElement(getElement().getChild(index));
        }
    }

    private class DeleteArrayElementAction extends VisibleElementAction {

        private int index;

        public DeleteArrayElementAction(int index) {
            super("Удалить элемент массива", "delete.png", KeyStroke.getKeyStroke("control DELETE")); //TODO set normal text
            this.index = index;
        }

        @SuppressWarnings({"SuspiciousSystemArraycopy"})
        @Override
        public void run(StructuredEditorModel model) {
            //delete element in the index
            Class<?> fieldType = getFieldType();
            Class<?> componentType = fieldType.getComponentType();

            //get value
            Object value = getValue();

            //add element to the end
            int length = Array.getLength(value);
            Object newValue = Array.newInstance(componentType, -1 + length);

            System.arraycopy(value, 0, newValue, 0, index);
            System.arraycopy(value, index + 1, newValue, index, length - index - 1);

            setValue(newValue);
            updateElement();

            int selectIndex = index == 0 ? 0 : index - 1;
            getModel().moveCaretToElement(getElement().getChild(selectIndex));
        }
    }

}