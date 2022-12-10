package cn.langpy.simsearch.util;


import cn.langpy.simsearch.annotation.IndexColumn;
import cn.langpy.simsearch.annotation.IndexId;
import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.model.IndexItem;
import org.apache.lucene.document.Document;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ReflectUtil {
    public static Logger log = Logger.getLogger(ReflectUtil.class.toString());
    private final static List<Class<?>> baseTypes = Arrays.asList(Integer.class, Double.class, Float.class, String.class, Boolean.class, List.class, Math.class);

    public ReflectUtil() {
        throw new RuntimeException("tool class can not be initialized!");
    }

    public static Object getFieldValue(Field field,Object o) {
        field.setAccessible(true);
        try {
            Object indexColumnValue = field.get(o);
            return indexColumnValue;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            field.setAccessible(false);
        }
        return null;
    }

    public static void checkParamValue(Object indexParamValue) {
        Class<?> aClass = indexParamValue.getClass();
        if (baseTypes.contains(aClass)) {
            throw new RuntimeException("can not create index for base types:" + baseTypes);
        }
    }

    public static IndexContent toIndexContent(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        IndexContent indexContent = new IndexContent();
        List<IndexItem> indexItems = new ArrayList<>();
        for (int j = 0; j < fields.length; j++) {
            Field field = fields[j];
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            IndexId indexId = field.getAnnotation(IndexId.class);
            if (indexId != null) {
                Object indexIdColumnValue = getFieldValue(field, entity);
                indexContent.setIdName(field.getName());
                indexContent.setIdValue(indexIdColumnValue + "");
                continue;
            }
            IndexColumn indexColumn = field.getAnnotation(IndexColumn.class);
            if (indexColumn != null) {
                Object indexColumnValue = ReflectUtil.getFieldValue(field, entity);
                IndexItem indexItem = new IndexItem();
                indexItem.setName(field.getName());
                indexItem.setValue(indexColumnValue + "");
                indexItems.add(indexItem);
            }
        }
        if (indexContent.getIdName()==null || indexContent.getIdValue()==null) {
            log.severe("can not create index for "+entity+",cause it does not have @IndexId or value");
        }
        indexContent.setEntitySource(entity.getClass());
        indexContent.setItems(indexItems);
        return indexContent;
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
                    if (field.getGenericType() == Integer.class) {
                        columnMethod.invoke(re, Integer.valueOf(v));
                    } else if (field.getGenericType() == Double.class) {
                        columnMethod.invoke(re, Double.valueOf(v));
                    } else if (field.getGenericType() == Float.class) {
                        columnMethod.invoke(re, Float.valueOf(v));
                    } else if (field.getGenericType() == Long.class) {
                        columnMethod.invoke(re, Long.valueOf(v));
                    } else if (field.getGenericType() == String.class) {
                        columnMethod.invoke(re, v);
                    } else {
                        log.warning("can not set " + fieldName + "'s value,cause its type is " + field.getGenericType());
                    }
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

    public static <T> List<T> transToReturnId(List<Document> documents, Class<?> returnClass) {
        List<T> returns = new ArrayList<>();
        for (Document document : documents) {
            for (Field field : returnClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                IndexId indexId = field.getAnnotation(IndexId.class);
                if (indexId != null) {
                    String fieldName = field.getName();
                    String v = document.get(fieldName);
                    if (v == null) {
                        continue;
                    }
                    if (field.getGenericType() == Integer.class) {
                        returns.add((T) Integer.valueOf(v));
                    } else if (field.getGenericType() == Double.class) {
                        returns.add((T) Double.valueOf(v));
                    } else if (field.getGenericType() == Float.class) {
                        returns.add((T) Float.valueOf(v));
                    } else if (field.getGenericType() == Long.class) {
                        returns.add((T) Long.valueOf(v));
                    } else if (field.getGenericType() == String.class) {
                        returns.add((T) v);
                    } else {
                        log.warning("can not set " + fieldName + "'s value,cause its type is " + field.getGenericType());
                    }
                    break;
                }
            }
        }

        return returns;
    }


}
