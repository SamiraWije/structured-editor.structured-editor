package ru.ipo.structurededitor.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 11.07.11
 * Time: 1:06
 */
public abstract class TwoSidedCellRenderer extends JPanel implements ListCellRenderer {

    protected static enum RenderStyle {
        NothingFound,
        ShortcutAndDescription
    }

    private static final Border border = new EmptyBorder(2, 2, 2, 4);
    private static final int innerSpace = 2; //space between icon and left text
    private static final Dimension zeroDimension = new Dimension(0, 0);

    private static final Color highlightColor = new Color(0xDD00DD);

//    private JList list;

    //some render settings, too little settings by now, but other may be added later
    protected int errorTextStyle;
    protected Color errorTextColor;

    protected String leftText;
    protected String rightText;
    protected String searchString;
    protected RenderStyle renderStyle;
    protected Icon icon;
    protected boolean isSelected;
    protected boolean rightTextToTheRight = true;

    public TwoSidedCellRenderer() {
        setBorder(border);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (list == null)
            return null;

        this.isSelected = isSelected;

        setupRenderData(value, index);

        //set up component
        if (this.isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());

        return this;
    }

    protected abstract void setupRenderData(Object value, int index);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        ((Graphics2D) g).setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
        );

        switch (renderStyle) {
            case NothingFound:
                drawNothingFound(g);
                break;
            case ShortcutAndDescription:
                drawShortcutAndDescription(g);
                break;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = zeroDimension;
        switch (renderStyle) {
            case NothingFound:
                d = preferredNothingFound();
                break;
            case ShortcutAndDescription:
                d = preferredShortcutAndDescription();
                break;
        }

//        Dimension minimumSize = list.getMinimumSize();
//        if (minimumSize != null)
//            d.width = Math.max(d.width, minimumSize.width);

        return d;
    }

    private void drawNothingFound(Graphics g) {
        Font font = getFont().deriveFont(errorTextStyle);
        FontMetrics fm = getFontMetrics(font);
        g.setFont(font);

        Insets insets = getInsets();

        g.setColor(errorTextColor);
        g.drawString(leftText, insets.left, insets.top + fm.getMaxAscent());
    }

    private void drawShortcutAndDescription(Graphics g) {
        //use no insets
        Font leftFont = getLeftTextFont();
        Font rightFont = getRightTextFont();

        FontMetrics leftFm = getFontMetrics(leftFont);
        FontMetrics rightFm = getFontMetrics(rightFont);

        Insets insets = getInsets();

        int outputtedWidth = insets.left;

        //draw label
        if (icon != null) {
            icon.paintIcon(this, g, insets.left, insets.top);

            outputtedWidth += icon.getIconWidth() + innerSpace;
        }

        if (leftText != null && leftText.length() > 0) {
            //g.setFont(leftFont);

            AttributedString left = getSearchHighlightedString(leftText, searchString, leftFont);
            g.setColor(getForeground());

            int textTop;
            if (icon == null)
                textTop = insets.top;
            else
                textTop = insets.top + (icon.getIconHeight() - leftFm.getHeight()) / 2;

            g.drawString(left.getIterator(), outputtedWidth, textTop + leftFm.getMaxAscent());

            outputtedWidth += leftFm.stringWidth(leftText);
            outputtedWidth += leftFm.stringWidth(" ");
        }

        if (rightText != null && rightText.length() > 0) {
            //g.setFont(rightFont);

            int rightTextWidth = rightFm.stringWidth(rightText);
            AttributedString right = getSearchHighlightedString(rightText, searchString, rightFont);
            g.setColor(Color.blue);

            int textTop;
            if (icon == null)
                textTop = insets.top;
            else
                textTop = insets.top + (icon.getIconHeight() - leftFm.getHeight()) / 2;

            if (rightTextToTheRight)
                g.drawString(
                        right.getIterator(),
                        getWidth() - rightTextWidth - insets.right,
                        textTop + rightFm.getMaxAscent()
                );
            else
                g.drawString(
                        right.getIterator(),
                        outputtedWidth,
                        textTop + rightFm.getMaxAscent()
                );
        }
    }

    private Dimension preferredNothingFound() {
        Insets insets = getInsets();

        Font font = getFont();
        FontMetrics fm = getFontMetrics(font);
        return new Dimension(
                fm.stringWidth(leftText) + insets.left + insets.right,
                fm.getHeight() + insets.left + insets.right
        );
    }

    private Dimension preferredShortcutAndDescription() {
        Font leftFont = getLeftTextFont();
        Font rightFont = getRightTextFont();

        FontMetrics leftFm = getFontMetrics(leftFont);
        FontMetrics rightFm = getFontMetrics(rightFont);

        int width = 0;
        int iconHeight = 0;

        if (icon != null) {
            width += icon.getIconWidth();
            iconHeight = icon.getIconHeight();
            width += innerSpace;
        }

        if (leftText != null && leftText.length() != 0) {
            width += leftFm.stringWidth(leftText);
            width += leftFm.stringWidth(" ");
        }

        if (rightText != null && rightText.length() != 0) {
            width += rightFm.stringWidth(rightText);
        }

        int height = Math.max(iconHeight, Math.max(leftFm.getHeight(), rightFm.getHeight()));

        Insets insets = getInsets();

        return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
    }

    private AttributedString getSearchHighlightedString(String text, String searchString, Font font) {
        AttributedString string = new AttributedString(text);

        string.addAttribute(TextAttribute.FONT, font);

        if (searchString == null || searchString.length() == 0)
            return string;

        int ind = text.toLowerCase().indexOf(searchString.toLowerCase());
        if (ind < 0)
            return string;

        string.addAttribute(TextAttribute.FOREGROUND, highlightColor, ind, ind + searchString.length());

        return string;
    }

    private Font getLeftTextFont() {
        return getFont().deriveFont(Font.BOLD);
    }

    private Font getRightTextFont() {
        return getFont().deriveFont(Font.PLAIN);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    //overridden as suggested by default list cell renderer
    @Override
    public void validate() {}
    @Override
    public void invalidate() {}
    @Override
    public void repaint() {}
    @Override
    public void revalidate() {}
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {}
    @Override
    public void repaint(Rectangle r) {}
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // Strings get interned...
//        if (propertyName == "text"
//                || ((propertyName == "font" || propertyName == "foreground")
//                && oldValue != newValue
//                && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {
//            super.firePropertyChange(propertyName, oldValue, newValue);
//        }
    }
    @Override
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
    @Override
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
    @Override
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
    @Override
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
    @Override
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

    //public static class UIResource extends DefaultListCellRenderer implements javax.swing.plaf.UIResource {}
}
