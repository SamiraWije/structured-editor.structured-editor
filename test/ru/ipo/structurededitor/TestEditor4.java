package ru.ipo.structurededitor;

import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.testLang.testLang.*;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 17:05:46
 */
public class TestEditor4 {

    public static void main(String[] args) {
        new TestEditor4();
    }

    public TestEditor4() {
        JFrame f = new JFrame("Test Editor");

        Bean2 bean2 = new Bean2();
        final StructuredEditorModel model = createModel(bean2);
        StructuredEditor structuredEditor = new StructuredEditor(model);

        f.setLayout(new BorderLayout());

        f.add(new JScrollPane(structuredEditor), BorderLayout.CENTER);
        f.add(structuredEditor.getActionsListComponent(), BorderLayout.SOUTH);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(640, 480);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        /*f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                ((CompositeElement)model.getRootElement()).add(new TextElement(TestEditor.this.model, "!!!"),0);

                System.out.println("e.getKeyCode() = " + e.getKeyCode());
                System.out.println("e.getKeyChar() = '" + e.getKeyChar() + "' (" + (int)e.getKeyChar() + ")");
                System.out.println("e.getModifiers() = " + e.getModifiers());
                System.out.println("e.getModifiersEx() = " + e.getModifiersEx());

                System.out.println();
            }

            public void keyReleased(KeyEvent e) {
            }
        });*/

        //model.getRootElement().gainFocus(new TextPosition(0,0), false, false);
    }

    private StructuredEditorModel createModel(DSLBean bean2) {
        StructuredEditor.initializeStructuredEditorUI();

        DSLBeansRegistry reg = new DSLBeansRegistry();

        reg.registerBean(Bean1.class);
        reg.registerBean(Bean2.class);
        reg.registerBean(BeanA.class);
        reg.registerBean(BeanA1.class);
        reg.registerBean(BeanA2.class);
        reg.registerBean(BeanA3.class);

        StructuredEditorModel model = new StructuredEditorModel(bean2);
        model.setBeansRegistry(reg);
        return model;
    }
}
