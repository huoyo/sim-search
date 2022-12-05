package cn.langpy.simsearch.util;


import org.apache.lucene.document.Document;

import java.lang.reflect.Field;
import java.util.List;

public class ReflectUtil {

    public static Object getFieldValue(Field field) {
        field.setAccessible(true);
        try {
            Object indexColumnValue = field.get(field.getName());
            return indexColumnValue;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            field.setAccessible(false);
        }
        return null;
    }

    public static  <T> List<T> transToReturnObject(List<Document> documents,Class<T> returnClass) {
        return null;
    }
}
