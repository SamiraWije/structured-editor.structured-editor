package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;
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
public class DSLBeanEditorV3 extends FieldEditor implements PropertyChangeListener {

    private final boolean isAbstract;

    private final VisibleElementAction removeAction =
            new VisibleElementAction("Удалить объект", "delete.png", KeyStroke.getKeyStroke("control DELETE")) { //TODO get text from data
                @Override
                public void run(StructuredEditorModel model) {
                    setValue(null);
                    updateElement(); //don't know why set does not do update
                }
            };

    private final VisibleElementAction createBeanAction = new VisibleElementAction("Создать объект", "add.png", KeyStroke.getKeyStroke("ENTER")) { //TODO get text from data
        @Override
        public void run(StructuredEditorModel model) {
            initializeNewBean(getMaskedFieldType());
        }
    };

    private void initializeNewBean(Class<?> beanType) {
        DSLBean bean;
        try {
            bean = (DSLBean) beanType.newInstance();
        } catch (Exception e) {
            throw new Error("Failed to instantiate bean");
        }

        setValue(bean);
        updateElement(); //don't know why set does not do update
    }

    public DSLBeanEditorV3(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, true, model);
        setModificationVector(model.getModificationVector());

        isAbstract = Modifier.isAbstract(getMaskedFieldType().getModifiers());

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
        TextElement element = new TextElement(getModel(), "[пусто]"); //TODO get text from data

        element.addAction(createBeanAction);

        return element;
    }

    private VisibleElement createCompletionElement() {
        AutoCompleteTextElement element = new AutoCompleteTextElement(getModel(), getAutoCompleteElements());

        element.addPropertyChangeListener("selectedValue", this);

        return element;
    }

    @Override
    protected void updateElement() {
        VisibleElement innerComponent = createInnerComponent();
        ((ContainerElement) getElement()).setSubElement(innerComponent);
        getModel().setFocusedElementAndCaret(innerComponent);
    }

    private List<AutoCompleteElement> getAutoCompleteElements() {
        Class<? extends DSLBean> fieldType = getMaskedFieldType().asSubclass(DSLBean.class);

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
            else {
                setValue(null);
                updateElement();
            }
        }
    }
}
