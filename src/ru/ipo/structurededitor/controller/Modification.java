package ru.ipo.structurededitor.controller;

import ru.ipo.structurededitor.model.DSLBean;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.11.2010
 * Time: 15:09:59
 */
public class Modification {
    private DSLBean bean;
    private String fieldName;
    private FieldMask mask;

    private Object oldValue;
    private Object newValue;

    private boolean userIntended;

    public Modification(DSLBean bean, String fieldName, FieldMask mask, Object oldValue, Object newValue, boolean userIntended) {
        this.bean = bean;
        this.fieldName = fieldName;
        this.mask = mask;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.userIntended = userIntended;
    }

    public DSLBean getBean() {
        return bean;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public FieldMask getMask() {
        return mask;
    }

    public boolean isUserIntended() {
        return userIntended;
    }
}
