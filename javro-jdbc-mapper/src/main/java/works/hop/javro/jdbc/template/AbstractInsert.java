package works.hop.javro.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.annotation.Column;
import works.hop.javro.jdbc.annotation.Embedded;
import works.hop.javro.jdbc.annotation.Id;
import works.hop.javro.jdbc.annotation.JoinColumn;
import works.hop.javro.jdbc.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.*;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.*;

public class AbstractInsert {

    static final Logger log = LoggerFactory.getLogger(AbstractInsert.class);
    public final Map<String, Object> fields = new LinkedHashMap<>();
    public final Map<String, AbstractInsert> joinFields = new LinkedHashMap<>();
    public final Map<String, List<AbstractInsert>> collectionJoinFields = new LinkedHashMap<>();

    public AbstractInsert(Object entity) {
        Class<?> targetClass = entity.getClass();
        do {
            Field[] fields = targetClass.getDeclaredFields();
            Arrays.stream(fields).filter(ReflectionUtil::isAcceptableField)
                    .filter(field -> !field.isAnnotationPresent(Id.class))
                    .forEach(field -> {
                        if (field.isAnnotationPresent(Embedded.class)) {
                            String embeddedFieldName = getColumnName(field);
                            Object embeddedFieldValue = getFieldValue(field, entity);
                            if (embeddedFieldValue != null) {
                                AbstractUpdate abstractUpdate = new AbstractUpdate(embeddedFieldValue);
                                this.fields.putAll(abstractUpdate.fields);
                                log.info("Added embedded field '{}' in entity object", embeddedFieldName);
                            }
                        } else if (field.isAnnotationPresent(Column.class)) {
                            String columnName = getColumnName(field);
                            this.fields.put(columnName, getFieldValue(field, entity));
                        } else if (field.isAnnotationPresent(JoinColumn.class)) {
                            String joinColumnName = getJoinColumnName(field);
                            if (field.getAnnotation(JoinColumn.class).updatable()) {
                                if (!isCollectionField(field) && field.getAnnotation(JoinColumn.class).fkTable().equals("")) {
                                    Object joinColumnFieldValue = getFieldValue(field, entity);
                                    if (joinColumnFieldValue != null) {
                                        this.joinFields.put(joinColumnName, new AbstractInsert(joinColumnFieldValue));
                                        Map<String, Optional<Object>> idColumnValues =
                                                getIdColumnValues(joinColumnFieldValue.getClass(), joinColumnFieldValue);
                                        if (!idColumnValues.isEmpty()) {
                                            idColumnValues.forEach((key, value) -> {
                                                value.ifPresent(o -> this.fields.put(joinColumnName, o));
                                            });
                                        }
                                    }
                                } else {
                                    Collection<?> joinColumnFieldValue = (Collection<?>) getFieldValue(field, entity);
                                    if (joinColumnFieldValue != null) {
                                        List<AbstractInsert> collectionEntities = new LinkedList<>();
                                        for (Object itemInCollection : joinColumnFieldValue) {
                                            collectionEntities.add(new AbstractInsert(itemInCollection));
                                        }
                                        this.collectionJoinFields.put(joinColumnName, collectionEntities);
                                    }
                                }
                            }
                        } else {
                            String columnName = getColumnName(field);
                            this.fields.put(columnName, getFieldValue(field, entity));
                        }
                    });
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != Object.class);
    }
}
