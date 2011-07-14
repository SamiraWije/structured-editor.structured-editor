package ru.ipo.structurededitor.view;

import javax.swing.*;
import java.awt.*;

/**
 * Обёртка для Graphics которая выводит текст в своей системе координат
 */
public class Display {

    private final Graphics g;
    private final StructuredEditorUI ui;

    public Display(Graphics g, StructuredEditorUI ui) {

        //TODO make antialiasing depending on monitor type
        //antialiasing by http://download.oracle.com/javase/tutorial/2d/text/renderinghints.html
        ((Graphics2D) g).setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
        );

        this.g = g;
        this.ui = ui;
        g.setFont(UIManager.getFont("StructuredEditor.font"));
    }

    /**
     * Вывод текста
     *
     * @param s  текст для вывода
     * @param x  столбец
     * @param y  строка
     * @param tp форматирование текста (цвет, жирность)
     */
    public void drawString(String s, int x, int y, TextProperties tp) {
        Font f = UIManager.getFont("StructuredEditor.font");
        if (f.getStyle() != tp.getStyle())
            f = f.deriveFont(tp.getStyle());

        g.setFont(f);
        g.setColor(tp.getColor());
        //System.out.println("STRING: " + s + " " + xToPixels(x) + "," + yToPixels(y));        
        g.drawString(s, xToPixels(x), yToPixels(y) + ui.getCharAscent());
    }

    public Graphics getGraphics() {
        return g;
    }

    public StructuredEditorUI getUi() {
        return ui;
    }

    public int xToPixels(int x) {
        return ui.xToPixels(x);
    }

    public int yToPixels(int y) {
        return ui.yToPixels(y);
    }
}
