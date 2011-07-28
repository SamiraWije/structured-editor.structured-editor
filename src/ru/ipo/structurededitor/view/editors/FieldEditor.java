package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.controller.Modification;
import ru.ipo.structurededitor.controller.ModificationHistory;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Базовый класс для компонент редактирования ПОЯ при помощи редактора
 */
public abstract class FieldEditor {

    //TODO think may be better replace Object with DSLBean
    private Object o;
    private String fieldName;
    private final StructuredEditorModel model;

    private VisibleElement editorElement;

    private FieldMask mask;

    private EditorSettings settings;

    public FieldEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        this.o = o;
        this.fieldName = fieldName;
        this.mask = mask;
        this.model = model;
        this.settings = settings;
    }

    public FieldMask getMask() {
        return mask;
    }

    public void setMask(FieldMask mask) {
        this.mask = mask;
        updateElement();
    }

    public VisibleElement getElement() {
        return editorElement;
    }

    protected void setElement(VisibleElement editorElement) {
        this.editorElement = editorElement;
    }

    protected Object getObject() {
        return o;
    }

    protected void setObject(Object o) {
        this.o = o;
    }

    protected String getFieldName() {
        return fieldName;
    }

    protected void setValue(Object value) {
        setValue(value, true);
    }

    protected void setValue(Object value, boolean userIntended) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            Method rm = pd.getReadMethod();
            Method wm = pd.getWriteMethod();
            Object oldUnmaskedValue = rm.invoke(getObject());

            Object oldValue;
            if (mask != null)
                oldValue = mask.get(oldUnmaskedValue);
            else
                oldValue = oldUnmaskedValue;

            if (oldUnmaskedValue == value)
                return;
            if (oldUnmaskedValue != null && oldUnmaskedValue.equals(value))
                return;

            ModificationHistory modificationHistory = model.getModificationHistory();

            modificationHistory.add(new Modification(
                    (DSLBean) getObject(),
                    fieldName,
                    mask,
                    oldValue,
                    value,
                    userIntended
            ));

            if (mask == null)
                wm.invoke(getObject(), value);
            else
                wm.invoke(getObject(), mask.set(oldUnmaskedValue, value));

        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.setValue()");
        }
    }

    protected Object getValue() {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            Method wm = pd.getReadMethod();
            Object value = wm.invoke(getObject());
            if (mask != null) {
                return mask.get(value);
            } else
                return value;
        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.getValue()");
        }
    }

    private Class<?> getUnmaskedFieldType() {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            return pd.getPropertyType();
        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.getValueType()");
        }
    }

    protected Class<?> getFieldType() {
        Class<?> fieldType = getUnmaskedFieldType();
        return mask == null ? fieldType : mask.getValueClass(fieldType);
    }

    public StructuredEditorModel getModel() {
        return model;
    }

    /**
     * Returns settings of the specified types. It is usually a good idea to implement getSettings() in
     * subclasses that calls getSettings(settingsClass)
     *
     * @param settingsClass type of settings
     * @return settings
     */
    protected <T extends EditorSettings> T getSettings(Class<T> settingsClass) {
        if (settings == null) {
            try {
                settings = settingsClass.newInstance();
            } catch (Exception ignored) {
            }
        }

        return (T) settings;
    }

    protected abstract void updateElement();
}
