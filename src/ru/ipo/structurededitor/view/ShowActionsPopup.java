package ru.ipo.structurededitor.view;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.actions.ActionsListComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 01.08.11
 * Time: 2:23
 */
public class ShowActionsPopup extends JButton implements ActionListener {

    private static Icon icon = new ImageIcon(ShowActionsPopup.class.getResource("images/ktip.png"));

    private StructuredEditor editor;
    private JScrollPane actionsComponentScroll;

    public ShowActionsPopup(StructuredEditor editor) {
        this.editor = editor;
        setIcon(icon);
        addActionListener(this);

        actionsComponentScroll = new JScrollPane(); //component to show is not crated by now
        actionsComponentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        setPreferredSize(new Dimension(icon.getIconWidth() + 2, icon.getIconHeight() + 2));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionsListComponent actionsComponent = editor.getActionsListComponent();

        if (actionsComponentScroll.getViewport().getView() == null) {
            actionsComponent.setConstantVisibleActions(false);
            actionsComponentScroll.setViewportView(actionsComponent);
        }

        editor.getModel().showPopup(actionsComponentScroll);
    }
}
