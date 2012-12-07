package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Created by IntelliJ IDEA.
 * User: olegperch
 * Date: 09.05.12
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class PictureTextElement extends TextElement implements ImageObserver {

    private Image picture;
    private Display d = null;
    private Dimension dimension = new Dimension(3, 3);


    public boolean isPicVisible() {
        return picVisible;
    }

    public void setPicVisible(boolean picVisible) {
        this.picVisible = picVisible;
    }

    private boolean picVisible = true;

    public Image getPicture() {
        return picture;
    }

    public void updateShowActionVisibility() {
        if (picture == null) {
          removeAction(showImageAction);
          removeAction(hideImageAction);
        } else {
            if (isPicVisible()) {
                removeAction(showImageAction);
                addActionToTheBeginning(hideImageAction);
            } else {
                addActionToTheBeginning(showImageAction);
                removeAction(hideImageAction);
            }
        }
    }

    private final VisibleElementAction selectFileAction = new VisibleElementAction("Выбрать новый файл изображения",
            "properties.png", "control SPACE") {
        @Override
        public void run(StructuredEditorModel model) {
            setText(getModel().selectImage());
            updateShowActionVisibility();
        }
    };
    private final VisibleElementAction showImageAction = new VisibleElementAction("Показать изображение",
            "information.png", "ENTER") {
        @Override
        public void run(StructuredEditorModel model) {
            setPicVisible(true);
            updateShowActionVisibility();
            setWidth(countWidth());
            setHeight(countHeight());
        }
    };
    private final VisibleElementAction hideImageAction = new VisibleElementAction("Спрятать изображение",
            "information.png", "ENTER") {
        @Override
        public void run(StructuredEditorModel model) {
            setPicVisible(false);
            updateShowActionVisibility();
            setWidth(countWidth());
            setHeight(countHeight());
        }
    };


    @Override
    protected int countHeight() {
        int h = super.countHeight();
        int ih = 0;
        if (picture != null && isPicVisible()) {
            ih = dimension.height + 1;
        }
        return h + ih;
    }

    @Override
    protected int countWidth() {
        int w = super.countWidth();
        int iw = 0;
        if (picture != null && isPicVisible()) {
            iw = dimension.width + 1;
        }
        return Math.max(w, iw);
    }

    public void setPicture(Image picture) {
        this.picture = picture;
        setHeight(countHeight());
        setWidth(countWidth());
    }

    public PictureTextElement(StructuredEditorModel model) {
        super(model);
        addAction(selectFileAction);
    }

    public PictureTextElement(StructuredEditorModel model, String fileName) {
        super(model, fileName);
    }


    @Override
    public void drawElement(int x0, int y0, Display d) {
        super.drawElement(x0, y0, d);
        if (picture != null && isPicVisible()) {

            if (d.getGraphics().drawImage(picture, d.xToPixels(x0), d.yToPixels(y0 + 1), d.xToPixels(dimension.width),
                    d.yToPixels(dimension.height), this)) {
                //System.out.println("Image is successfully loaded");
            } else {
                System.out.println("Error in loading of Image!");
            }
        }
        this.d = d;
    }

    @Override
    protected void processMouseEvent(java.awt.event.MouseEvent e) {
        if (e.getID() == java.awt.event.MouseEvent.MOUSE_CLICKED) {
            if (getText() == null || getText().equals("")
                    || picture == null) {
                selectFileAction.run(getModel());
            } else {
                if (e.getClickCount() == 2) {
                    setPicVisible(!isPicVisible());
                }
            }
            setHeight(countHeight());
            setWidth(countWidth());
            updateShowActionVisibility();
        }
        super.processMouseEvent(e);
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & ABORT) == 0) {
            System.out.println("Error in the loading of image " + getText() + "!!");
            return false;
        }
        if ((infoflags & ALLBITS) == 0) {
            System.out.println("Image " + getText() + " is loading . . .");
            return true;
        } else {
            System.out.println("Image " + getText() + " is loaded.");
            return false;
        }
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }
}
