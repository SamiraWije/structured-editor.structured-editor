package ru.ipo.structurededitor.model;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 28.07.2011
 * Time: 20:00:00
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EnumFieldParams {
    String displayText();
}
