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

    public static final Object EMPTY_LIST_OBJECT = new Object();

    private final Collection<AutoCompleteElement> elementsToSelect;

    private final List<AutoCompleteElement> filteredElements = new ArrayList<AutoCompleteElement>();

    private String searchString = null;
    private int maxShortcutWidth;
    private int maxDescriptionWidth;

    private final MatchResult EMPTY_MATCH = new MatchResult(-1, -1);

    public AutoCompleteListModel(AutoCompleteElement... elementsToSelect) {
        this(Arrays.asList(elementsToSelect));
    }

    public AutoCompleteListModel(Collection<AutoCompleteElement> elementsToSelect) {
        //wrap beans
        this.elementsToSelect = elementsToSelect;

        filterBeans();
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

    private void filterBeans() {
        filteredElements.clear();

        for (AutoCompleteElement element : elementsToSelect)
            if (matchFilter(element) != null)
                filteredElements.add(element);

        updateMaxWidths();
    }

    @Override
    public int getSize() {
        int size = filteredElements.size();
        if (size == 0)
            size = 1;
        return size;
    }

    @Override
    public Object getElementAt(int index) {
        if (filteredElements.size() == 0)
            return EMPTY_LIST_OBJECT;

        return filteredElements.get(index);
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
        filterBeans();
    }

    public String getSearchString() {
        return searchString;
    }

    private MatchResult matchFilter(AutoCompleteElement element) {
        if (searchString == null)
            return EMPTY_MATCH;

        int si = element.getShortcut().indexOf(searchString);
        int di = element.getDescription().indexOf(searchString);

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
