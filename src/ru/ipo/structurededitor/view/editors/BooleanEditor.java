package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.settings.BooleanSettings;
import ru.ipo.structurededitor.view.elements.TextElement;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class BooleanEditor extends FieldEditor {

    public BooleanEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);

        final TextElement editorElement = new TextElement(model);

        editorElement.addAction(new VisibleElementAction(getSettings().getChangeActionText(), "properties.png", KeyStroke.getKeyStroke("SPACE")) {
            @Override
            public void run(StructuredEditorModel model) {
                Boolean value = (Boolean) getValue();
                if (value != null && value)
                    setValue(false);
                else
                    setValue(true);
                updateElement();
                model.moveCaretToElement(editorElement);
            }
        });

        setElement(editorElement);

        updateElement();
    }

    private BooleanSettings getSettings() {
        return getSettings(BooleanSettings.class);
    }

    @Override
    protected void updateElement() {
        TextElement editorElement = (TextElement)getElement();
        Boolean value = (Boolean) getValue();

        if (value == null)
            value = false; //TODO think of editing of null value for Boolean type

        editorElement.setText(value ? getSettings().getTrueText() : getSettings().getFalseText());
    }

}