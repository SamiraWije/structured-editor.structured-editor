package ru.ipo.structurededitor.controller;

import java.util.EventObject;

/**
 * Modification in editor
 */
public class ModificationEvent extends EventObject {
    Modification mod;

    public Modification getMod() {
        return mod;
    }

    public ModificationEvent(Object source, Modification mod) {
        super(source);
        this.mod = mod;

    }

}
