package ru.ipo.structurededitor.controller;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 13.07.11
 * Time: 22:47
 */
public class MaskComposition implements FieldMask {

    private FieldMask mask1;
    private FieldMask mask2;

    /**
     * Создает маску, которая является композицией масок <code>mask1</code> и <code>mask2</code>. Маски применяются
     * в порядке: <code>fieldValue <- mask1 <- mask2 <- field</code>
     * @param mask1 первая маска
     * @param mask2 вторая маска
     */
    public static FieldMask composeMasks(FieldMask mask1, FieldMask mask2) {
        if (mask1 == null)
            return mask2;
        else if (mask2 == null)
            return mask1;
        else
            return new MaskComposition(mask1, mask2);
    }

    private MaskComposition(FieldMask mask1, FieldMask mask2) {
        this.mask1 = mask1;
        this.mask2 = mask2;
    }

    @Override
    public Object get(Object field) {
        return mask1.get(mask2.get(field));
    }

    @Override
    public Object set(Object field, Object value) {
        Object masked2 = mask2.get(field);
        Object newMasked2 = mask1.set(masked2, value);
        return mask2.set(field, newMasked2);
    }

    @Override
    public Class getValueClass(Class fieldClass) {
        return mask1.getValueClass(mask2.getValueClass(fieldClass));
    }
}