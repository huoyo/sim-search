package cn.langpy.simsearch.util;


import org.apache.lucene.document.Document;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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

    public static <T> List<T> transToReturnObject(List<Document> documents, Class<T> returnClass) {
        List<T> returns = new ArrayList<>();
        try {
            for (Document document : documents) {
                T re = returnClass.newInstance();
                for (Field field : returnClass.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                        continue;
                    }
                    String fieldName = field.getName();
                    String v = document.get(fieldName);
                    if (v == null) {
                        continue;
                    }
                    PropertyDescriptor columnPd = new PropertyDescriptor(fieldName, returnClass);
                    Method columnMethod = columnPd.getWriteMethod();
                    columnMethod.invoke(re, v);
                }
                returns.add(re);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return returns;
    }
}
