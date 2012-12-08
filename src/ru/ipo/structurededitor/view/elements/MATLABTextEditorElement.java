package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 07.08.2012
 * Time: 20:33:25
 * To change this template use File | Settings | File Templates.
 */
public class MATLABTextEditorElement extends TextEditorElement {
    public MATLABTextEditorElement(StructuredEditorModel model, String text, boolean singleLined) {
        super(model, text, singleLined);
        StructuredEditor panelEditor = (StructuredEditor) (getModel().getApp());
        if (panelEditor != null) {
            panelEditor.getModel().addPropertyChangeListener("focusedElement",
                    new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            CaretData caretData = getElementCaret();
                            String text = getText();
                            if (text == null)
                                text = "";

                            StringBuilder sb = new StringBuilder();

                            int sPos = caretData.stringPosition;
                            if (sPos>-1)
                                sb.append(text.substring(0, sPos));
                            //append whitespaces if cursor is to the right of the last line symbol, but it's not the null/empty text
                            if (!text.equals(""))
                                for (int i = caretData.columnNormalized; i < caretData.column; i++)
                                    sb.append(' ');
                            VisibleElement selectedVisibleElement = (VisibleElement) (evt.getNewValue());
                            String clip = "";
                            /*if (selectedVisibleElement instanceof AutoCompleteTextElement) {

                                if (((AutoCompleteTextElement) selectedVisibleElement).getText() != null)

                            }
                            else*/

                            if (selectedVisibleElement instanceof TextElement) {
                                clip = ((TextElement) selectedVisibleElement).getText();
                                if (clip==null) clip="";
                            }
                            sb.append(clip);
                            int newCaretPos = sb.length();
                            if (sPos>-1)
                                sb.append(text.substring(sPos));

                            setText(sb.toString());
                            getCaretByPosition(newCaretPos, caretData);
                        }
                    });
            panelEditor.getModel().updateModel();
        }
    }

    public MATLABTextEditorElement(StructuredEditorModel model, String text) {
        this(model, text, true);
    }

    public MATLABTextEditorElement(StructuredEditorModel model) {
        this(model, null, true);
    }
}
