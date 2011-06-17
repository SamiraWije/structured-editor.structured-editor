package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.TextElement;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 25.03.11
 * Time: 13:46
 */
public class FileEditor extends FieldEditor {

    public static final int MAX_FILE_LENGTH = 10 * 1024 * 1024; // 10 Mb
    public static final int BUFFER_SIZE = 4 * 1024; // 4 Kb

    public FileEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, model);

        //wtf ???
        setModificationVector(model.getModificationVector());

        TextElement editorElement = new TextElement(model);
        setElement(editorElement);

        editorElement.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (Character.isLetter(e.getKeyChar()) || e.getKeyChar() == ' ')
                    showSelector();
            }
        });

        updateElement();
    }

    private void showSelector() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(null);//TODO make editor to be the parent component
        if (res != JFileChooser.APPROVE_OPTION)
            return;

        File file = fc.getSelectedFile();

        if (file.length() > MAX_FILE_LENGTH)
            return; //TODO think of error message or use internal message informer

        //try load file as byte array
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
            ByteArrayOutputStream out = new ByteArrayOutputStream((int)file.length());
            byte[] buffer = new byte[BUFFER_SIZE];
            int read; //pp of read
            while ((read = in.read(buffer)) >= 0)
                out.write(buffer, 0, read);

            setValue(out.toByteArray());

            TextElement element = (TextElement) getElement();
            element.setText(file.getAbsolutePath());
        } catch (IOException ignored) {
            //do nothing
        }
    }

    @Override
    protected void updateElement() {
        TextElement element = (TextElement) getElement();
        element.setText("[некоторые данные]");
    }
}
