package ru.ipo.structurededitor.controller;

import java.lang.reflect.Array;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.12.10
 * Time: 16:15
 */
public class ArrayFieldMask implements FieldMask {
    private int index;

    public ArrayFieldMask(int index) {
        this.index = index;
    }

    public Object get(Object field) {
        if (index < Array.getLength(field))
            return Array.get(field, index);
        else
            return null;
    }

    public Object set(Object field, Object value) {
        Array.set(field, index, value);
        return field;
    }

    public Class getValueClass(Class fieldClass) {
        return fieldClass.getComponentType();
    }
}
