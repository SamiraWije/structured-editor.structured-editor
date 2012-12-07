package ru.ipo.structurededitor.view.events;

import java.util.EventObject;

/**
 * Event: loading of image from file
 */
public class ImageLoadEvent extends EventObject {
    String fileName;
    public ImageLoadEvent(Object source, String fileName) {
        super(source);
        this.fileName=fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
