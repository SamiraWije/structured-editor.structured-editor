package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.model.EnumFieldParams;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextProperties;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;
import ru.ipo.structurededitor.view.editors.settings.EnumSettings;
import ru.ipo.structurededitor.view.elements.AutoCompleteTextElement;
import ru.ipo.structurededitor.view.elements.ContainerElement;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class EnumEditor extends FieldEditor {

    private TextProperties enumTextProperties = new TextProperties(
            Font.BOLD,
            UIManager.getColor("StructuredEditor.text.edit.color")
    );

    private VisibleElementAction selectOtherValueAction;

    private VisibleElementAction removeValueAction;

    private final PropertyChangeListener selectionListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            ContainerElement element = (ContainerElement) getElement();
            Object selectedValue = ((AutoCompleteTextElement) element.getSubElement()).getSelectedValue();
            setValue(selectedValue);
            updateElement();
            getModel().moveCaretToElement(getElement());
        }
    };

    public EnumEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);

        ContainerElement element = new ContainerElement(model, createInnerElement());

        setElement(element);

        createActions();
    }

    private void createActions() {
        selectOtherValueAction = new VisibleElementAction(getSettings().getSelectOtherVariantActionText(), "properties.png", KeyStroke.getKeyStroke("control SPACE")) {
            @Override
            public void run(StructuredEditorModel model) {
                setValue(null);
                updateElement();

                ContainerElement element = (ContainerElement) getElement();

                model.moveCaretToElement(element);

                ((AutoCompleteTextElement) element.getSubElement()).popup();
            }
        };

        removeValueAction = new VisibleElementAction("Очистить выбор", "delete.png", KeyStroke.getKeyStroke("control DELETE")) { //TODO set normal text
            @Override
            public void run(StructuredEditorModel model) {
                setValue(null);
                updateElement();
                model.moveCaretToElement(getElement());
            }
        };
    }

    private VisibleElement createInnerElement() {
        Enum value = (Enum) getValue();

        if (value == null) {

            List<AutoCompleteElement> completionElements;
            try {
                completionElements = createCompletionElements();
            } catch (Exception e) {
                throw new Error("Failed to create enum editor");
            }

            AutoCompleteTextElement element = new AutoCompleteTextElement(getModel(), completionElements);
            element.setNullText(getSettings().getNullText());
            element.setShowPopupActionText(getSettings().getSelectVariantActionText());

            element.addPropertyChangeListener("selectedValue", selectionListener);

            return element;
        } else {
            TextElement element = new TextElement(getModel(), getDisplayTextForEnumValue(value));

            element.setTextProperties(enumTextProperties);

            element.addAction(selectOtherValueAction);
            element.addAction(removeValueAction);

            return element;
        }
    }

    private List<AutoCompleteElement> createCompletionElements() throws Exception {
        Class<? extends Enum> fieldType = getFieldType().asSubclass(Enum.class);

        Enum[] values = fieldType.getEnumConstants();

        ArrayList<AutoCompleteElement> elements = new ArrayList<AutoCompleteElement>();

        for (final Enum value : values) {
            final String description = getDisplayTextForEnumValue(value);

            elements.add(new AutoCompleteElement() {
                @Override
                public Object getValue() {
                    return value;
                }

                @Override
                public String getShortcut() {
                    return "";
                }

                @Override
                public String getDescription() {
                    return description;
                }
            });
        }

        return elements;
    }

    private String getDisplayTextForEnumValue(Enum value) {
        try {
            Class valueClass = value.getDeclaringClass();
            Field field = valueClass.getField(value.name());
            EnumFieldParams fieldParams = field.getAnnotation(EnumFieldParams.class);
            return fieldParams == null ? value.toString() : fieldParams.displayText();
        } catch (NoSuchFieldException ignored) { //not possible to occur
            return "IDEA RULEZZZ";
        }
    }

    private EnumSettings getSettings() {
        return getSettings(EnumSettings.class);
    }

    @Override
    protected void updateElement() {
        ContainerElement element = (ContainerElement) getElement();
        element.setSubElement(createInnerElement());
    }

}