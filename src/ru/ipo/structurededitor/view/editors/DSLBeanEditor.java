package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;
import ru.ipo.structurededitor.view.editors.settings.AbstractDSLBeanSettings;
import ru.ipo.structurededitor.view.editors.settings.DSLBeanSettings;
import ru.ipo.structurededitor.view.elements.*;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class DSLBeanEditor extends FieldEditor implements PropertyChangeListener {

    private final boolean isAbstract;

    private VisibleElementAction removeAction;

    private VisibleElementAction createBeanAction;

    private void initializeNewBean(Class<?> beanType) {
        DSLBean bean;
        try {
            bean = (DSLBean) beanType.newInstance();
        } catch (Exception e) {
            throw new Error("Failed to instantiate bean");
        }

        setValue(bean);
    }

    public DSLBeanEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);

        final Class<?> beanType = getFieldType();
        isAbstract = Modifier.isAbstract(beanType.getModifiers());

        if (isSetNullActionNeeded())
            removeAction = new VisibleElementAction(getSetNullActionText(), "delete.png", KeyStroke.getKeyStroke("control DELETE")) {
                @Override
                public void run(StructuredEditorModel model) {
                    setValue(null);
                    updateElement(); //don't know why set does not do update
                }
            };

        if (!isAbstract)
            createBeanAction = new VisibleElementAction(getSettings().getCreateBeanActionText(), "add.png", KeyStroke.getKeyStroke("ENTER")) {
                @Override
                public void run(StructuredEditorModel model) {
                    initializeNewBean(beanType);
                    updateElement();
                }
            };

        //if null is not allowed, then set non-null value
        if (!isAbstract && !getSettings().isNullAllowed()) {
            if (getValue() == null) {
                initializeNewBean(beanType);
            }
        }

        ContainerElement ce = new ContainerElement(model, createInnerComponent());
        setElement(ce);
    }

    private VisibleElement createInnerComponent() {
        Object value = getValue();

        if (value != null) {
            EditorRenderer renderer = new EditorRenderer(getModel(), (DSLBean) value);
            VisibleElement element = renderer.getRenderResult();
            element.addAction(removeAction);
            return element;
        } else if (isAbstract) {
            return createCompletionElement();
        } else {
            return createNullElement();
        }
    }

    private VisibleElement createNullElement() {
        TextElement element = new TextElement(getModel(), getSettings().getNullText());

        element.addAction(createBeanAction);

        return element;
    }

    private VisibleElement createCompletionElement() {
        AutoCompleteTextElement element = new AutoCompleteTextElement(getModel(), getAutoCompleteElements());

        element.addPropertyChangeListener("selectedValue", this);

        AbstractDSLBeanSettings abstractSettings = getAbstractSettings();

        element.setShowPopupActionText(abstractSettings.getSelectVariantActionText());
        element.setEmptyText(abstractSettings.getNullValueText());
        element.setNullText(abstractSettings.getNullValueText());

        return element;
    }

    @Override
    protected void updateElement() {
        VisibleElement innerComponent = createInnerComponent();
        ((ContainerElement) getElement()).setSubElement(innerComponent);
        getModel().moveCaretToElement(innerComponent);
    }

    private List<AutoCompleteElement> getAutoCompleteElements() {
        Class<? extends DSLBean> fieldType = getFieldType().asSubclass(DSLBean.class);

        List<Class<? extends DSLBean>> subclasses = getModel().getBeansRegistry().getAllSubclasses(fieldType, true);

        List<AutoCompleteElement> elements = new ArrayList<AutoCompleteElement>(subclasses.size());

        for (final Class<? extends DSLBean> subclass : subclasses) {
            elements.add(new AutoCompleteElement() {
                @Override
                public Object getValue() {
                    return subclass;
                }

                @Override
                public String getShortcut() {
                    return getBeanShortcut(subclass);
                }

                @Override
                public String getDescription() {
                    return getBeanDescription(subclass);
                }
            });
        }

        return elements;
    }

    private static String getBeanShortcut(Class<? extends DSLBean> beanClass) {
        DSLBeanParams annotation = beanClass.getAnnotation(DSLBeanParams.class);
        if (annotation == null || annotation.shortcut() == null)
            return beanClass.getSimpleName();
        else
            return annotation.shortcut();
    }

    private static String getBeanDescription(Class<? extends DSLBean> beanClass) {
        DSLBeanParams annotation = beanClass.getAnnotation(DSLBeanParams.class);
        if (annotation == null || annotation.description() == null)
            return "";
        else
            return annotation.description();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("selectedValue")) {
            Class<?> selectedBeanClass = (Class<?>) evt.getNewValue();
            if (selectedBeanClass != null)
                initializeNewBean(selectedBeanClass);
            else
                setValue(null);

            updateElement();
        }
    }

    private DSLBeanSettings getSettings() {
        return getSettings(DSLBeanSettings.class);
    }

    private AbstractDSLBeanSettings getAbstractSettings() {
        return getSettings(AbstractDSLBeanSettings.class);
    }

    private boolean isSetNullActionNeeded() {
        return isAbstract || getSettings().isNullAllowed();
    }

    private String getSetNullActionText() {
        return isAbstract ? getAbstractSettings().getSetNullActionText() : getSettings().getSetNullActionText();
    }
}
