package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;
import ru.ipo.structurededitor.view.elements.TextEditorElement;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class StringEditor extends FieldEditor {

    private final VisibleElementAction setNullAction = new VisibleElementAction("Удалить текст", "delete.png", KeyStroke.getKeyStroke("control DELETE")) {
        @Override
        public void run(StructuredEditorModel model) {
            setValue(null);
            updateElement();
            getModel().moveCaretToElement(getElement());
        }
    };

    public StringEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);

        final TextEditorElement editorElement;
        editorElement = new TextEditorElement(model, null, getSettings().isSingleLine());

        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String newValue = (String) evt.getNewValue();
                setValue(newValue);
                updateSetNullActionVisibility(editorElement, newValue);
            }
        });

        setElement(editorElement);

        updateElement();
    }

    private StringSettings getSettings() {
        return getSettings(StringSettings.class);
    }

    @Override
    protected void updateElement() {
        TextEditorElement textElement = (TextEditorElement) getElement();
        String value = (String) getValue();
        textElement.setText(value);

        updateSetNullActionVisibility(textElement, value);
    }

    private void updateSetNullActionVisibility(TextEditorElement textElement, String value) {
        if (value == null)
            textElement.removeAction(setNullAction);
        else
            textElement.addAction(setNullAction);
    }

}