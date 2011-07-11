package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.ComboBoxTextEditorElement;
import ru.ipo.structurededitor.view.elements.ContainerElement;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;
import ru.ipo.structurededitor.view.events.ComboBoxSelectListener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class DSLBeanEditorV2 extends FieldEditor {

    private TextElement innerElement;
    private StructuredEditorModel model;
    private boolean isAbstract;

    private static final KeyStroke deleteStroke = KeyStroke.getKeyStroke("control DELETE");

    /**
     * Returns text that is shown when value is null
     * @return string to display when value is null
     */
    private String getEmptyText() {
        Class<?> fieldType = getFieldType();

        FieldMask mask = getMask();
        if (mask != null)
            fieldType = mask.getValueClass(fieldType);

        String result = fieldType.getSimpleName();

        DSLBeanParams annotation = fieldType.getAnnotation(DSLBeanParams.class);
        if (annotation != null) {
            String description = annotation.description();
            if (description != null)
                result = description;
        }

        return "[" + result + "]";
    }

    private void setNonAbstractInnerElement() {
        innerElement.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if (Character.isLetterOrDigit(e.getKeyChar())) {
                    resetValue();
                }
            }

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }
        });
    }

    private void resetValue() {
        try {
            setValue(
                    getMaskedFieldType().newInstance()
            );
            updateElement();
        } catch (Exception e1) {
            throw new Error("Failed to instantiate bean: " + e1);
        }
    }

    public DSLBeanEditorV2(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, model);
        this.model = model;

        setModificationVector(model.getModificationVector());

        isAbstract = Modifier.isAbstract(getMaskedFieldType().getModifiers());

        if (isAbstract) {
            innerElement = createBeanSelectionElement(model);
        } else {
            innerElement = new TextElement(model, "");
            innerElement.setEmptyString(getEmptyText());
        }

        final ContainerElement container = new ContainerElement(model, innerElement);
        if (isAbstract) {
            setComboBoxList();
        } else {
            setNonAbstractInnerElement();
        }
        container.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if (deleteStroke.equals(KeyStroke.getKeyStrokeForEvent(e))) {
                    if (getValue() != null) {
                        container.setSubElement(innerElement);
                        setValue(null);
                        e.consume();
                    }
                }
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        setElement(container);
        updateElement();
    }

    private void setComboBoxList() {
        ComboBoxTextEditorElement<Class<? extends DSLBean>> beanClassSelectionElement =
                (ComboBoxTextEditorElement<Class<? extends DSLBean>>) innerElement;
        List<Class<? extends DSLBean>> classes;
        classes = model.getBeansRegistry().getAllSubclasses((Class<? extends DSLBean>) getMaskedFieldType(), true);
        for (Class<? extends DSLBean> clazz : classes) {
            DSLBeanParams beanParams = clazz.getAnnotation(DSLBeanParams.class);
            if (beanParams == null) {
                beanClassSelectionElement.addValue(clazz.getSimpleName(), "", clazz);
            } else {
                beanClassSelectionElement.addValue(beanParams.shortcut(), beanParams.description(), clazz);
            }
        }
    }

    private ComboBoxTextEditorElement<Class<? extends DSLBean>> createBeanSelectionElement(final StructuredEditorModel model) {
        final ComboBoxTextEditorElement<Class<? extends DSLBean>> res = new ComboBoxTextEditorElement<Class<? extends DSLBean>>(model);
        res.setEmptyString(getEmptyText());

        res.addComboBoxSelectListener(new ComboBoxSelectListener() {
            public void itemSelected() {
                Class<? extends DSLBean> value = res.getValue();
                if (value != null) {
                    setNewBean(value, model);
                }
            }
        });
        res.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Class<? extends DSLBean> value = res.getValue();
                    if (value != null) {
                        setNewBean(value, model);
                        e.consume();
                    }
                }

            }

            public void keyTyped(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        return res;
    }

    private void setNewBean(Class<? extends DSLBean> beanClass, StructuredEditorModel model) {
        try {
            DSLBean bean = beanClass.newInstance();
            setValue(bean);
            updateElement();
        } catch (Exception e) {
            throw new Error("Failed to initialize DSL Bean");
        }
    }

    @Override
    protected void updateElement() {
        if (model == null)
            return;
        final ContainerElement container = (ContainerElement) getElement();

        final ComboBoxTextEditorElement<Class<? extends DSLBean>> beanClassSelectionElement;

        if (isAbstract) {
            beanClassSelectionElement =
                    (ComboBoxTextEditorElement<Class<? extends DSLBean>>) innerElement;
        } else {
            beanClassSelectionElement = null;
        }

        Object value = getValue();
        if (value == null) {
            if (beanClassSelectionElement != null)
                beanClassSelectionElement.setText("");
            container.setSubElement(innerElement);
        } else {
            EditorRenderer renderer = new EditorRenderer(model, (DSLBean) value);
            container.setSubElement(renderer.getRenderResult());
        }
    }

}
