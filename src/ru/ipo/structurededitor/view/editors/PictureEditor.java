package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.settings.PictureSettings;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;
import ru.ipo.structurededitor.view.elements.PictureTextElement;
import ru.ipo.structurededitor.view.elements.TextEditorElement;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class PictureEditor extends FieldEditor {

    private VisibleElementAction setNullAction;

    public PictureEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);

        final PictureTextElement editorElement;
        editorElement = new PictureTextElement(model);

        editorElement.setEmptyText(getSettings().getEmptyText());
        editorElement.setNullText(getSettings().getNullText());
        editorElement.setToolTipText(getSettings().getToolTipText());
        editorElement.setDimension(getSettings().getDimension());

        createActions();

        addTextChangedListener(editorElement);

        setElement(editorElement);

        if (getValue() == null && !getSettings().isNullAllowed())
            setValue("", false);

        updateElement();
    }

    private void addTextChangedListener(final PictureTextElement editorElement) {
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String newValue = (String) evt.getNewValue();
                setValue(newValue);
                if (newValue != null && !newValue.equals("")) {
                    Image img = getModel().loadImage(newValue);
                    editorElement.setPicture(img);
                } else {
                    editorElement.setPicture(null);
                }
                updateSetNullActionVisibility(editorElement, newValue);
                editorElement.updateShowActionVisibility();
            }
        });
    }

    private void createActions() {
        String actionText = getSettings().isNullAllowed() ? "Удалить картинку" : "Очистить имя файла";

        setNullAction = new VisibleElementAction(actionText, "delete.png", "control DELETE") {
            @Override
            public void run(StructuredEditorModel model) {
                setValue(getSettings().isNullAllowed() ? null : "");
                updateElement();
                getModel().moveCaretToElement(getElement());
            }
        };
    }

    private PictureSettings getSettings() {
        return getSettings(PictureSettings.class);
    }

    @Override
    protected void updateElement() {
        PictureTextElement textElement = (PictureTextElement) getElement();
        String value = (String) getValue();
        textElement.setText(value);
        if (value != null && !value.equals("")) {
            Image img = getModel().loadImage(value);
            textElement.setPicture(img);
        }
        updateSetNullActionVisibility(textElement, value);
    }

    private void updateSetNullActionVisibility(PictureTextElement textElement, String value) {
        if (value == null || value.equals(""))
            textElement.removeAction(setNullAction);
        else
            textElement.addAction(setNullAction);
    }

}