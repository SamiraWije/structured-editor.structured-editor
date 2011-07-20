package ru.ipo.structurededitor.model;

/**
 * Этот интерфейс помечает классы, хранящие информацию для редакторов.
 */
public interface EditorSettings {

}

//package ru.ipo.structurededitor.model;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by IntelliJ IDEA.
// * User: ilya
// * Date: 19.07.11
// * Time: 17:46
// */
//public class EditorSettings {
//
//    public static String ALLOW_NULL = "allow null";
//    public static String NULL_EDITOR_TEXT = "null editor text";
//    public static String EMPTY_EDITOR_TEXT = "empty editor text";
//
//    private Map<Object, Object> settings;
//
//    public EditorSettings(Object... data) {
//        if (data == null || data.length == 0 || data.length % 2 == 1)
//            throw new IllegalArgumentException("Constructor should get positive even number of parameters.");
//
//        settings = new HashMap<Object, Object>();
//
//        for (int i = 0; i < data.length; i += 2) {
//            settings.put(data[i], data[i + 1]);
//        }
//    }
//
//    public EditorSettings(Map<Object, Object> settings) {
//        this.settings = new HashMap<Object, Object>(settings);
//    }
//
//    public Object get(Object key) {
//        return settings.get(key);
//    }
//
//    public Object get(Object key, Object defaultValue) {
//        Object value = settings.get(key);
//        return value == null ? defaultValue : value;
//    }
//
//    public Boolean allowNull() {
//        return (Boolean) get(ALLOW_NULL);
//    }
//
//    public String nullText() {
//        return (String) get(NULL_EDITOR_TEXT);
//    }
//
//    public String emptyText() {
//        return (String) get(EMPTY_EDITOR_TEXT);
//    }
//
//}
