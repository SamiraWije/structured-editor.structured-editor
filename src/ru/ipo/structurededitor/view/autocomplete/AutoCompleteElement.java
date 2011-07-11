package ru.ipo.structurededitor.view.autocomplete;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 09.07.11
 * Time: 15:50
 */
public interface AutoCompleteElement {

    public Object getValue();

    /**
     * Returns element shortcut, always returns not null
     * @return element shortcut
     */
    public String getShortcut();

    /**
     * Returns element description, always returns not null
     * @return element description
     */
    public String getDescription();

}
