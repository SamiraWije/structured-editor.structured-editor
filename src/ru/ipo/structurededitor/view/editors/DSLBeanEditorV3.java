package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteElement;
import ru.ipo.structurededitor.view.autocomplete.AutoCompleteListComponent;
import ru.ipo.structurededitor.view.elements.ContainerElement;
import ru.ipo.structurededitor.view.elements.TextEditorElement;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import javax.swing.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class DSLBeanEditorV3 extends FieldEditor {

    private final boolean isAbstract;

    private final VisibleElementAction removeAction =
            new VisibleElementAction("Удалить объект", "delete.png", KeyStroke.getKeyStroke("DELETE")) { //TODO get text from data
        @Override
        public void run(StructuredEditorModel model) {
            setValue(null);
            updateElement(); //don't know why set does not do update
        }
    };

    private final VisibleElementAction createBeanAction = new VisibleElementAction("Создать объект", "add.png", KeyStroke.getKeyStroke("ENTER")) { //TODO get text from data
        @Override
        public void run(StructuredEditorModel model) {
            Class<?> fieldType = getMaskedFieldType();
            DSLBean bean;
            try {
                bean = (DSLBean) fieldType.newInstance();
            } catch (Exception e) {
                throw new Error("Failed to instantiate bean");
            }

            setValue(bean);
            updateElement(); //don't know why set does not do update
        }
    };

    private final VisibleElementAction showPopupAction = new VisibleElementAction("Выбрать вариант", "properties.png", KeyStroke.getKeyStroke("control SPACE")) { //TODO get text from data
        @Override
        public void run(StructuredEditorModel model) {
            Class<? extends DSLBean> fieldType = getMaskedFieldType().asSubclass(DSLBean.class);

            List<Class<? extends DSLBean>> subclasses = model.getBeansRegistry().getAllSubclasses(fieldType, true);

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

            model.showPopup(AutoCompleteListComponent.getComponent(elements, null));
        }
    };

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
        TextEditorElement element = new TextEditorElement(getModel(), "[пусто]"); //TODO get text from data

        element.addAction(showPopupAction);

        return element;
    }

    @Override
    protected void updateElement() {
        setElement(new ContainerElement(getModel(), createInnerComponent()));
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

}
