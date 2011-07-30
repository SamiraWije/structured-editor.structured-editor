package ru.ipo.structurededitor.view;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 07.07.11
 * Time: 22:29
 */
public class PopupSupport {

    private JComponent parent;
    private Popup popup;

    public PopupSupport(JComponent parent) {
        this.parent = parent;
    }

    public void show(JComponent component, int x, int y) {
        hide();

        popup = PopupFactory.getSharedInstance().getPopup(parent, component, x, y);
        popup.show();
    }

    public void hide() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

}
