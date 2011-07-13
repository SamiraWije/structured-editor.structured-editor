package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;
import ru.ipo.structurededitor.view.elements.AutoCompleteTextElement;
import ru.ipo.structurededitor.view.elements.ContainerElement;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class EnumEditorV2 extends FieldEditor {

    private VisibleElementAction selectOtherValueAction = new VisibleElementAction("Выбрать другой элемент", "properties.png", KeyStroke.getKeyStroke("control SPACE")) { //TODO set normal text
        @Override
        public void run(StructuredEditorModel model) {
            setValue(null);
            updateElement();

            ContainerElement element = (ContainerElement) getElement();

            ((AutoCompleteTextElement) element.getSubElement()).popup();
        }
    };

    private VisibleElementAction removeValueAction = new VisibleElementAction("Очистить выбор", "delete.png", KeyStroke.getKeyStroke("control DELETE")) { //TODO set normal text
        @Override
        public void run(StructuredEditorModel model) {
            setValue(null);
            updateElement();
        }
    };

    private final PropertyChangeListener selectionListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            ContainerElement element = (ContainerElement) getElement();
            Object selectedValue = ((AutoCompleteTextElement) element.getSubElement()).getSelectedValue();
            setValue(selectedValue);
            updateElement();
        }
    };

    public EnumEditorV2(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, true, model);

        setModificationVector(model.getModificationVector());

        ContainerElement element = new ContainerElement(model, createInnerElement());

        setElement(element);
    }

    private VisibleElement createInnerElement() {
        Object value = getValue();

        if (value == null) {

            List<AutoCompleteElement> completionElements;
            try {
                completionElements = createCompletionElements();
            } catch (Exception e) {
                throw new Error("Failed to create enum editor");
            }

            AutoCompleteTextElement element = new AutoCompleteTextElement(getModel(), completionElements);

            element.addPropertyChangeListener("selectedValue", selectionListener);

            return element;
        } else {
            TextElement element = new TextElement(getModel(), value.toString()); //TODO set normal text

            element.addAction(selectOtherValueAction);
            element.addAction(removeValueAction);

            return element;
        }
    }

    private List<AutoCompleteElement> createCompletionElements() throws Exception {
        Class<?> fieldType = getMaskedFieldType();
        //call values() method to obtain array of values
        Method valuesMethod = fieldType.getMethod("values");
        Enum[] values = (Enum[]) valuesMethod.invoke(null);

        List<AutoCompleteElement> elements = new ArrayList<AutoCompleteElement>();

        for (final Enum value : values)
            elements.add(new AutoCompleteElement() {
                @Override
                public Object getValue() {
                    return value;
                }

                @Override
                public String getShortcut() {
                    return value.toString();
                }

                @Override
                public String getDescription() {
                    return "Нету описания";
                }
            });

        return elements;
    }

    @Override
    protected void updateElement() {
        ContainerElement element = (ContainerElement) getElement();
        element.setSubElement(createInnerElement());
    }

}