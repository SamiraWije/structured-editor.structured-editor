package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.ArrayFieldMask;
import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.controller.MaskComposition;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.settings.ArraySettings;
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
    //TODO if cursor is between elements, insert action is not available

    private EditorSettings itemsSettings;

    private VisibleElementAction clearArrayAction;
    private VisibleElementAction createArrayAction;

    public ArrayEditor(Object o, String fieldName, FieldMask mask, CompositeElement.Orientation orientation, char spaceChar, final StructuredEditorModel model, EditorSettings settings, EditorSettings itemsSettings) {
        super(o, fieldName, mask, model, settings);
        this.itemsSettings = itemsSettings;

        final CompositeElement arrayElement = new CompositeElement(model, orientation, spaceChar);

        setElement(arrayElement);

        ensureValidValue();

        createActions();

        updateElement();
    }

    private void ensureValidValue() {
        int minElements = getSettings().getMinElements();

        if (getValue() == null && ! getSettings().isNullAllowed())
            setValue(createArray(minElements), false);

        if (getValue() != null && Array.getLength(getValue()) < minElements)
            setValue(createArray(minElements), false);
    }

    private void createActions() {
        //Clear array action

        String clearActionText = getSettings().getClearArrayActionText();

        clearArrayAction = new VisibleElementAction(clearActionText, "delete.png", KeyStroke.getKeyStroke("control DELETE")) {
            @Override
            public void run(StructuredEditorModel model) {
                if (getSettings().isNullAllowed())
                    setValue(null);
                else
                    setValue(createArray(
                            getSettings().getMinElements()
                    ));

                updateElement();

                getModel().moveCaretToElement(getElement());
            }
        };

        //Create array action

        String createActionText = getSettings().getCreateArrayActionText();

        createArrayAction = new VisibleElementAction(createActionText, "add.png", KeyStroke.getKeyStroke("ENTER")) {
            @Override
            public void run(StructuredEditorModel model) {
                setValue(createArray(getSettings().getMinElements()));

                updateElement();

                getModel().moveCaretToElement(getElement());
            }
        };
    }

    private Object createArray(int size) {
        return Array.newInstance(getFieldType().getComponentType(), size);
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
            value = createArray(0);

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
                getObject().getClass().asSubclass(DSLBean.class),
                getFieldName(),
                getObject(),
                MaskComposition.composeMasks(new ArrayFieldMask(index), getMask()),
                getModel(),
                itemsSettings
        );
    }

    private VisibleElement createNullElement() {
        TextElement nullElement = new TextElement(getModel(), null);
        nullElement.setNullText(getSettings().getNullText());

        nullElement.addAction(createArrayAction);

        return nullElement;
    }

    private VisibleElement createZeroElement() {
        TextElement zeroElement = new TextElement(getModel(), null);
        zeroElement.setNullText(getSettings().getZeroElementsText());

        zeroElement.addAction(new InsertArrayElementAction(0));
        if (getSettings().isNullAllowed())
            zeroElement.addAction(clearArrayAction);

        return zeroElement;
    }

    @Override
    protected void updateElement() {
        Object value = getValue();

        CompositeElement root = (CompositeElement) getElement();

        root.clearActions();

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

        int maxElements = getSettings().getMaxElements();
        int minElements = getSettings().getMinElements();

        for (int ind = 0; ind < length; ind++) {
            FieldEditor editor = createEditorInstance(ind);
            VisibleElement element = editor.getElement();
            ContainerElement container = new ContainerElement(getModel(), element);

            if (length > minElements)
                container.addAction(new DeleteArrayElementAction(ind));

            if (length < maxElements)
                container.addAction(new InsertArrayElementAction(ind + 1));

            elements.add(container);
        }

        if (getSettings().isAllowClearFilledArray())
            root.addAction(clearArrayAction);

        root.setElements(elements);
    }

    private class InsertArrayElementAction extends VisibleElementAction {

        private int index;

        public InsertArrayElementAction(int index) {
            super(getSettings().getInsertActionText(), "add.png", KeyStroke.getKeyStroke("ENTER"));
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
            super(getSettings().getRemoveActionText(), "delete.png", KeyStroke.getKeyStroke("control DELETE"));
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

    private ArraySettings getSettings() {
        return getSettings(ArraySettings.class);
    }

}