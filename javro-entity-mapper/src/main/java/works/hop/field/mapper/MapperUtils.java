package works.hop.field.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.field.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static works.hop.field.reflect.ReflectionUtil.*;

public class MapperUtils {

    private static final Logger log = LoggerFactory.getLogger(MapperUtils.class);

    public static void set(Field field, Object target, Object value) {
        boolean isAccessible = field.canAccess(target);
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.warn("Could not SET value in field {} in object {}", field.getName(), target.getClass().getName());
        } finally {
            field.setAccessible(isAccessible);
        }
    }

    public static <T> T get(Field field, Object target) {
        boolean isAccessible = field.canAccess(target);
        try {
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.warn("Could not GET value from field {} in object {}", field.getName(), target.getClass().getName());
            return null;
        } finally {
            field.setAccessible(isAccessible);
        }
    }

    public static Field field(String fieldName, Class<?> type) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (type.getSuperclass() != Object.class) {
                return field(fieldName, type.getSuperclass());
            } else {
                log.warn("Could NOT find the field {}", fieldName);
                throw new RuntimeException(e);
            }
        }
    }

    public static String appendPath(String path, String fieldName) {
        String newPath = (path != null && path.length() > 0) ?
                (fieldName.startsWith("[") ?
                        String.format("%s%s", path, fieldName) :
                        String.format("%s.%s", path, fieldName)) :
                fieldName;
        log.info("LOGGING PATH - {}", newPath);
        return newPath;
    }

    public static <A, B> B mapAToB(A source, Class<B> target) {
        return mapAToB(source, target, new MapperContext(""));
    }

    public static <A, B> B mapAToB(A source, Class<B> target, MapperContext context) {
        Map<Object, Object> fieldsMap = inspectSource(source, source.getClass());
        System.out.println(fieldsMap);
        return mapField(fieldsMap, newInstance(target), "", context);
    }

    public static <T> T mapField(Map<Object, Object> fieldsMap, T target, String path, MapperContext context) {
        fieldsMap.forEach((key, value) -> {
            if (String.class.isAssignableFrom(key.getClass())) {
                if (ListContainer.class.isAssignableFrom(value.getClass())) {
                    String keyValue = context.resolveAtoB(path, key.toString());
                    Field targetField = MapperUtils.field(keyValue, target.getClass());
                    Object targetValue = mapCollection((ListContainer<Object>) fieldsMap.get(key), targetField.getType(), appendPath(path, key.toString()), context);
                    MapperUtils.set(targetField, target, targetValue);
                } else if (MapContainer.class.isAssignableFrom(value.getClass())) {
                    String keyValue = context.resolveAtoB(path, key.toString());
                    Field targetField = MapperUtils.field(keyValue, target.getClass());
                    Object targetValue = mapAssociation((MapContainer<Object, Object>) fieldsMap.get(key), appendPath(path, key.toString()), context);
                    MapperUtils.set(targetField, target, targetValue);
                } else if (Map.class.isAssignableFrom(value.getClass())) {
                    String keyValue = context.resolveAtoB(path, key.toString());
                    Field targetField = MapperUtils.field(keyValue, target.getClass());
                    Object targetValue = mapField((Map<Object, Object>) fieldsMap.get(key), newInstance(targetField.getType()), appendPath(path, key.toString()), context);
                    MapperUtils.set(targetField, target, targetValue);
                } else {
                    String keyValue = context.resolveAtoB(path, key.toString());
                    Field targetField = MapperUtils.field(keyValue, target.getClass());
                    MapperUtils.set(targetField, target, fieldsMap.get(key));
                }
            }
        });
        return target;
    }

    public static <T> Collection<T> mapCollection(ListContainer<Object> fieldsList, Class<T> type, String path, MapperContext context) {
        Collection<T> collection = newCollection(type);
        String typeName = fieldsList.genericType.getTypeName();
        AtomicInteger index = new AtomicInteger(0);
        fieldsList.forEach(element -> {
            try {
                String listIndex = String.format("[%d]", index.getAndIncrement());
                Object elementValue = mapField((Map<Object, Object>) element, newInstance(Class.forName(typeName)), appendPath(path, listIndex), context);
                collection.add((T) elementValue);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                log.warn("Could NOT create an instance of {}", typeName);
            }
        });
        return collection;
    }

    public static Map<Object, Object> mapAssociation(MapContainer<Object, Object> fieldsMap, String path, MapperContext context) {
        Map<Object, Object> collection = new HashMap<>();
        String keyTypeName = fieldsMap.genericType[0].getTypeName();
        String valueTypeName = fieldsMap.genericType[1].getTypeName();
        fieldsMap.forEach((key, value) -> {
            Object entryKey = null;
            try {
                Class<?> keyType = Class.forName(keyTypeName);
                if (isSimpleDataField(key.getClass())) {
                    entryKey = key;
                } else {
                    String mapKey = String.format("[\"%s\"]", keyType.getName());
                    entryKey = mapField((Map<Object, Object>) key, newInstance(keyType), appendPath(path, mapKey), context);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                log.warn("Could NOT create an instance of {}", keyTypeName);
            }

            Object entryValue = null;
            try {
                Class<?> valueType = Class.forName(valueTypeName);
                if (isSimpleDataField(value.getClass())) {
                    entryValue = value;
                } else {
                    String mapKey = String.format("[\"%s\"]", valueType.getName());
                    entryValue = mapField((Map<Object, Object>) value, newInstance(valueType), appendPath(path, mapKey), context);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                log.warn("Could NOT create an instance of {}", valueTypeName);
            }
            if (entryKey != null && entryValue != null) {
                collection.put(entryKey, entryValue);
            }
        });
        return collection;
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create instance of target type", e);
        }
    }

    public static Object newInstance(String className) {
        try {
            return Class.forName(className).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create instance of target type", e);
        }
    }

    public static <T> Collection<T> newCollection(Class<T> type) {
        if (type == List.class) {
            return new ArrayList<T>();
        } else if (type == Set.class) {
            return new HashSet<>();
        } else if (type == Queue.class) {
            return new LinkedList<>();
        } else {
            throw new RuntimeException("Could not determine type of collection class from " + type.getName());
        }
    }

    public static Map<Object, Object> inspectSource(Object source, Class<?> type) {
        Map<Object, Object> fieldsMap = new LinkedHashMap<>();
        Arrays.stream(type.getDeclaredFields()).filter(ReflectionUtil::isAcceptableField).forEach(field -> {
            if (isSimpleDataField(field.getType())) {
                Object fieldValue = MapperUtils.get(field, source);
                fieldsMap.put(field.getName(), fieldValue);
            } else if (isCollectionField(field)) {
                Object collectionValue = getFieldValue(field, source);
                Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                ListContainer<Object> mappedFieldValue = new ListContainer<>(genericType);
                if (collectionValue != null) {
                    ((Collection<?>) collectionValue).forEach(listItem -> {
                        Map<Object, Object> listItemFields = inspectSource(listItem, listItem.getClass());
                        mappedFieldValue.add(listItemFields);
                    });
                    fieldsMap.put(field.getName(), mappedFieldValue);
                }
            } else if (isAssociationField(field)) {
                Object mapValue = getFieldValue(field, source);
                Type[] genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                MapContainer<Object, Object> mappedFieldValue = new MapContainer<>(genericType);
                if (mapValue != null) {
                    ((Map<?, ?>) mapValue).forEach((key, value) -> {
                        Object keyData = isSimpleDataField(key.getClass()) ? key :
                                inspectSource(key, key.getClass());
                        Object valueData = isSimpleDataField(value.getClass()) ? value :
                                inspectSource(value, value.getClass());
                        mappedFieldValue.put(keyData, valueData);
                    });
                    fieldsMap.put(field.getName(), mappedFieldValue);
                }
            } else {
                Object complexType = getFieldValue(field, source);
                if (complexType != null) {
                    Map<Object, Object> complexFieldsMap = inspectSource(complexType, complexType.getClass());
                    fieldsMap.put(field.getName(), complexFieldsMap);
                }
            }
        });
        if (type.getSuperclass() != Object.class) {
            Map<Object, Object> superFieldsMap = inspectSource(source, type.getSuperclass());
            fieldsMap.putAll(superFieldsMap);
        }
        return fieldsMap;
    }

    public static <A, B> B setAToB(A source, Class<B> targetType, MapperContext context) {
        B targetInstance = newInstance(targetType);
        retrieveAndSet(source, source.getClass(), targetInstance, targetType, "", context);
        return targetInstance;
    }

    public static void retrieveAndSet(Object source, Class<?> sourceType, Object target, Class<?> targetType, String targetKey, MapperContext context) {
        Arrays.stream(sourceType.getDeclaredFields()).filter(ReflectionUtil::isAcceptableField).forEach(field -> {
            if (isSimpleDataField(field.getType())) {
                Object fieldValue = MapperUtils.get(field, source);
                //set value in target
                String mapperKey = field.getName();
                Optional<Field> optionalTargetField = getTargetField(context.resolveAtoB(targetKey, mapperKey), target, target.getClass());
                optionalTargetField.ifPresent(targetField -> set(targetField, target, fieldValue));
            } else if (isCollectionField(field)) {
                Object collectionValue = getFieldValue(field, source);
                //determine target field type
                Optional<Field> optionalTargetField = getTargetField(context.resolveAtoB(targetKey, field.getName()), target, target.getClass());
                if (optionalTargetField.isPresent()) {
                    Field targetField = optionalTargetField.get();
                    Type targetGenericType = ((ParameterizedType) targetField.getGenericType()).getActualTypeArguments()[0];
                    if (collectionValue != null) {
                        Collection<Object> targetCollectionInstance = newCollectionInstance(field.getType());
                        ((Collection<?>) collectionValue).forEach(listItem -> {
                            Object targetCollectionItem = newInstance(targetGenericType.getTypeName());
                            retrieveAndSet(listItem, listItem.getClass(), targetCollectionItem, targetCollectionItem.getClass(), targetField.getName(), (MapperContext) context.get(field.getName()));
                            targetCollectionInstance.add(targetCollectionItem);
                        });
                        //set field value
                        set(targetField, target, targetCollectionInstance);
                    }
                }
            } else if (isAssociationField(field)) {
                Object mapValue = getFieldValue(field, source);
                //determine target field type
                Optional<Field> optionalTargetField = getTargetField(context.resolveAtoB("", field.getName()), target, target.getClass());
                if (optionalTargetField.isPresent()) {
                    Field targetField = optionalTargetField.get();
                    Type[] targetGenericTypes = ((ParameterizedType) targetField.getGenericType()).getActualTypeArguments();
                    if (mapValue != null) {
                        Map<Object, Object> targetMapInstance = new HashMap<>();
                        ((Map<?, ?>) mapValue).forEach((key, value) -> {
                            Object keyData;
                            if (isSimpleDataField(key.getClass())) {
                                keyData = key;
                            } else {
                                Object targetKeyData = newInstance(targetGenericTypes[0].getTypeName());
                                retrieveAndSet(key, key.getClass(), targetKeyData, targetKeyData.getClass(), targetField.getName(), (MapperContext) context.get(field.getName()));
                                keyData = targetKeyData;
                            }
                            Object valueData;
                            if (isSimpleDataField(value.getClass())) {
                                valueData = value;
                            } else {
                                Object targetValueData = newInstance(targetGenericTypes[1].getTypeName());
                                retrieveAndSet(value, value.getClass(), targetValueData, targetValueData.getClass(), targetField.getName(), (MapperContext) context.get(field.getName()));
                                valueData = targetValueData;
                            }
                            targetMapInstance.put(keyData, valueData);
                        });
                        //set field value
//                        set(targetField, target, targetMapInstance);
                        Object resolvedValue = context.resolveAToB("", field.getName(), targetMapInstance);
                        set(targetField, target, resolvedValue);
                    }
                }
            } else {
                Object complexType = getFieldValue(field, source);
                if (complexType != null) {
                    Field targetField = field(context.resolveAtoB("", field.getName()), targetType);
                    Object targetComplexTypeInstance = newInstance(targetField.getType());
                    retrieveAndSet(complexType, complexType.getClass(), targetComplexTypeInstance, targetComplexTypeInstance.getClass(), targetField.getName(), (MapperContext) context.get(field.getName()));
                    //set field value
                    set(targetField, target, targetComplexTypeInstance);
                }
            }
        });
        if (sourceType.getSuperclass() != Object.class) {
            retrieveAndSet(source, sourceType.getSuperclass(), target, targetType, targetKey, context);
        }
    }
}
