package ru.ipo.structurededitor.controller;


import ru.ipo.structurededitor.model.DSLBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.11.2010
 * Time: 15:09:59
 *
 * <p>History of modifications, supports undo and redo operations.</p>
 * <p>History consists of two types of modifications: user intended and not user intended. Not user
 * intended modifications are done by editors if they do not support certain types of values
 * and replace them with other values without any actions performed by user.
 * </p>
 * <p>Let <code>U</code> denote user intended modification, <code>N</code> denote not user intended,
 * <code>|</code> denote position in history.
 * Possible positions of <code>|</code> may be: <code>NN|UNNN|U|UNN|</code>, i.e. it is not possible that
 * to the right of the position there is a not user intended modification.
 * </p>
 * <p>To store history of modifications and history of undone modifications, two deques are used.</p>
 */
public class ModificationHistory {

    private int historySize = 100;

    /**
     * A double ended queue to add modifications and remove them if history is too long
     */
    private Deque<Modification> history = new LinkedList<Modification>();

    /**
     * A queue to store undone changes that user may wish to redo
     */
    private Deque<Modification> future = new LinkedList<Modification>();

    private ModificationEventSupport mes = new ModificationEventSupport();

    public void add(Modification mod) {
        history.addLast(mod);

        future.clear();

        while (history.size() > historySize)
            history.removeFirst();

        mes.fireModification();
    }

    public boolean canUndo() {
        //undo is possible if history contains user intended actions

        for (Modification modification : history)
            if (modification.isUserIntended())
                return true;

        return false;
    }

    public boolean canRedo() {
        return ! future.isEmpty();
    }

    public void undo() {
        if (!canUndo())
            return;

        Modification mod;
        do {
            mod = history.removeLast();
            future.addFirst(mod);

            setValue(mod.getBean(), mod.getFieldName(), mod.getMask(), mod.getOldValue());
        } while (! mod.isUserIntended());

        mes.fireModification();
    }

    public void redo() {
        if (!canRedo())
            return;

        do {
            Modification mod = future.removeFirst();
            history.addLast(mod);

            setValue(mod.getBean(), mod.getFieldName(), mod.getMask(), mod.getNewValue());
        } while (!future.isEmpty() && !future.peekFirst().isUserIntended());

        mes.fireModification();
    }

    //TODO make method private because modifications history should clear by editor when needed
    public void clearVector() {
        history.clear();
        future.clear();

        mes.fireModification();
    }

    public void setHistorySize(int historySize) {
        if (historySize <= 0)
            throw new IllegalArgumentException("History size should be a positive integer: " + historySize);

        this.historySize = historySize;

        while (history.size() > historySize)
            history.pollLast();
    }

    private void setValue(DSLBean bean, String fieldName, FieldMask mask, Object value) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, bean.getClass());
            Method rm = pd.getReadMethod();
            Method wm = pd.getWriteMethod();

            if (mask == null) {
                wm.invoke(bean, value);
            } else {
                Object val = rm.invoke(bean);
                val = mask.set(val, value);
                wm.invoke(bean, val);
            }

        } catch (Exception e1) {
            throw new Error("Fail in Modification.setValue()");
        }
    }

    public void addModificationListener(ModificationListener l) {
        mes.addModificationListener(l);
    }

    public void removeModificationListener(ModificationListener l) {
        mes.removeModificationListener(l);
    }
}
