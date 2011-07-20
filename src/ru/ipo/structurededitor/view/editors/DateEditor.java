package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.TextElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 25.03.11
 * Time: 0:05
 */
public class DateEditor extends FieldEditor {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

    public DateEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);

        //wtf ???
        setModificationVector(model.getModificationVector());

        final TextElement editorElement = new TextElement(model);
        editorElement.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (Character.isLetter(e.getKeyChar()) || e.getKeyChar() == ' ')
                    showSelector();
            }
        });

        setElement(editorElement);

        updateElement();
    }

    private void showSelector() {
        final JSpinner timeSpinner = new JSpinner();
        timeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "yyyy:MM:dd HH:mm:ss");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(getValue()); // will only show the current time

        JButton closeButton = new JButton("Закрыть");

        final JDialog dialog = new JDialog((Window)null, "Выберите дату");
        dialog.setLayout(new BorderLayout());
        dialog.add(timeSpinner, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(null); //TODO try to make it relative to the editor
        dialog.pack();
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setValue(timeSpinner.getValue());
                updateElement();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(timeSpinner.getValue());
                updateElement();
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    @Override
    protected void updateElement() {
        TextElement editorElement = (TextElement) getElement();
        Date date = (Date) getValue();

        if (date == null)
            editorElement.setText(null);
        else
            editorElement.setText(DATE_FORMAT.format(date));
    }
}
