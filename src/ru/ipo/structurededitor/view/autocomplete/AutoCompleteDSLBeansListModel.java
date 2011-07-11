//package ru.ipo.structurededitor.view.autocomplete;
//
//import ru.ipo.structurededitor.model.DSLBean;
//import ru.ipo.structurededitor.model.DSLBeanParams;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//
///**
// * Created by IntelliJ IDEA.
// * User: ilya
// * Date: 09.07.11
// * Time: 16:19
// */
//public class AutoCompleteDSLBeansListModel extends AutoCompleteListModel {
//
//    public AutoCompleteDSLBeansListModel(Class<? extends DSLBean>... beansToSelect) {
//        this(Arrays.asList(beansToSelect));
//    }
//
//    public AutoCompleteDSLBeansListModel(Collection<Class<? extends DSLBean>> beansToSelect) {
//        super(convertBeansToAutoCompleteElements(beansToSelect));
//    }
//
//    private static Collection<AutoCompleteElement> convertBeansToAutoCompleteElements(Collection<Class<? extends DSLBean>> beansToSelect) {
//        ArrayList<AutoCompleteElement> list = new ArrayList<AutoCompleteElement>(beansToSelect.size());
//
//        for (final Class<? extends DSLBean> dslBean : beansToSelect) {
//            list.add(new AutoCompleteElement() {
//                @Override
//                public Object getValue() {
//                    return dslBean;
//                }
//
//                @Override
//                public String getShortcut() {
//                    return getBeanShortcut(dslBean);
//                }
//
//                @Override
//                public String getDescription() {
//                    return getBeanDescription(dslBean);
//                }
//            });
//        }
//
//        return list;
//    }
//
//    private static String getBeanShortcut(Class<? extends DSLBean> beanClass) {
//        DSLBeanParams annotation = beanClass.getAnnotation(DSLBeanParams.class);
//        if (annotation == null || annotation.shortcut() == null)
//            return beanClass.getSimpleName();
//        else
//            return annotation.shortcut();
//    }
//
//    private static String getBeanDescription(Class<? extends DSLBean> beanClass) {
//        DSLBeanParams annotation = beanClass.getAnnotation(DSLBeanParams.class);
//        if (annotation == null || annotation.description() == null)
//            return "";
//        else
//            return annotation.description();
//    }
//}
