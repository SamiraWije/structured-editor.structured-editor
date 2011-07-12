package ru.ipo.structurededitor.view.autocomplete;

import javax.swing.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 07.07.11
 * Time: 23:12
 */
public class AutoCompleteListModel extends AbstractListModel {

    private final List<AutoCompleteElement> elementsToSelect;

    private final List<AutoCompleteElement> filteredElements = new ArrayList<AutoCompleteElement>();

    private String searchString = null;
    private int maxShortcutWidth;
    private int maxDescriptionWidth;

    private final MatchResult EMPTY_MATCH = new MatchResult(-1, -1);

    public AutoCompleteListModel(AutoCompleteElement... elementsToSelect) {
        this(Arrays.asList(elementsToSelect));
    }

    public AutoCompleteListModel(List<AutoCompleteElement> elementsToSelect) {
        //wrap beans
        this.elementsToSelect = elementsToSelect;

        filterElements();
    }

    private void updateMaxWidths() {
        maxShortcutWidth = 0;
        maxDescriptionWidth = 0;
        for (AutoCompleteElement element : this.filteredElements) {
            int shortcutWidth = element.getShortcut().length();
            int descriptionWidth = element.getDescription().length();

            if (shortcutWidth > maxShortcutWidth)
                maxShortcutWidth = shortcutWidth;
            if (descriptionWidth > maxDescriptionWidth)
                maxDescriptionWidth = descriptionWidth;
        }
    }

    private void filterElements() {
        int oldSize = getSize();

        filteredElements.clear();

        for (AutoCompleteElement element : elementsToSelect)
            if (matchFilter(element) != null)
                filteredElements.add(element);

        updateMaxWidths();

        int newSize = getSize();

        fireIntervalRemoved(this, 0, oldSize - 1);
        fireIntervalAdded(this, 0, newSize - 1);
    }

    @Override
    public int getSize() {
        int size = filteredElements.size();
        if (size == 0)
            size = 1 + elementsToSelect.size();
        return size;
    }

    @Override
    public Object getElementAt(int index) {
        if (filteredElements.size() == 0) {
            if (index == 0)
                return null;
            else
                return elementsToSelect.get(index - 1);
        }

        return filteredElements.get(index);
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
        filterElements();
    }

    public String getSearchString() {
        return searchString;
    }

    private MatchResult matchFilter(AutoCompleteElement element) {
        if (searchString == null)
            return EMPTY_MATCH;

        String lowercaseSearch = searchString.toLowerCase();

        int si = element.getShortcut().toLowerCase().indexOf(lowercaseSearch);
        int di = element.getDescription().toLowerCase().indexOf(lowercaseSearch);

        if (si >= 0 || di >= 0)
            return new MatchResult(si, di);
        else
            return null;
    }

    public int getMaxShortcutWidth() {
        return maxShortcutWidth;
    }

    public int getMaxDescriptionWidth() {
        return maxDescriptionWidth;
    }

    public AutoCompleteElement getElementByShortcut(String shortcut) {
        //may be implemented by HashMap if this is slow

        for (AutoCompleteElement element : elementsToSelect) {
            if (element.getShortcut().equals(shortcut))
                return element;
        }

        return null;
    }

    public class MatchResult {

        private int shortcutMatchIndex;
        private int descriptionMatchIndex;

        private MatchResult(int shortcutMatchIndex, int descriptionMatchIndex) {
            this.shortcutMatchIndex = shortcutMatchIndex;
            this.descriptionMatchIndex = descriptionMatchIndex;
        }

        public int getMatchStringLength() {
            return searchString == null ? 0 : searchString.length();
        }

        public int getShortcutMatchIndex() {
            return shortcutMatchIndex;
        }

        public int getDescriptionMatchIndex() {
            return descriptionMatchIndex;
        }
    }

    public AutoCompleteElement getTheLongestElement() {
        int max = 0;
        AutoCompleteElement longestElement = null;
        for (AutoCompleteElement element : filteredElements) {
            int len = element.getShortcut().length() + element.getDescription().length();
            if (len > max) {
                max = len;
                longestElement = element;
            }
        }

        return longestElement;
    }
}
