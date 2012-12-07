package ru.ipo.structurededitor.view.events;

import java.awt.*;
import java.util.EventListener;

/**
 * Image load event listener
 */
public interface ImageLoadListener extends EventListener {

    public Image loadImage(ImageLoadEvent e);

    public String selectImage();
}
