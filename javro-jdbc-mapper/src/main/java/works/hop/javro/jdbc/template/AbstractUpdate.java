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

public class AbstractUpdate {

    static final Logger log = LoggerFactory.getLogger(AbstractUpdate.class);
    public final Map<String, Object> fields = new HashMap<>();
    public final Map<String, Object> idFields = new HashMap<>();
    public final Map<String, AbstractUpdate> joinFields = new HashMap<>();
    public final Map<String, List<AbstractUpdate>> collectionJoinFields = new HashMap<>();

    public AbstractUpdate(Object entity) {
        Class<?> targetClass = entity.getClass();
        do {
            Field[] fields = targetClass.getDeclaredFields();
            Arrays.stream(fields).filter(ReflectionUtil::isAcceptableField).forEach(field -> {
                if (field.isAnnotationPresent(Embedded.class)) {
                    String embeddedFieldName = getColumnName(field);
                    Object embeddedFieldValue = getFieldValue(field, entity);
                    if (embeddedFieldValue != null) {
                        AbstractUpdate abstractUpdate = new AbstractUpdate(embeddedFieldValue);
                        this.fields.putAll(abstractUpdate.fields);
                        this.idFields.putAll(abstractUpdate.idFields);
                        log.info("Added embedded field '{}' in entity object", embeddedFieldName);
                    }
                } else if (field.isAnnotationPresent(Id.class)) {
                    String columnName = getColumnName(field);
                    this.idFields.put(columnName, getFieldValue(field, entity));
                } else if (field.isAnnotationPresent(Column.class)) {
                    String columnName = getColumnName(field);
                    if (field.getAnnotation(Column.class).updatable()) {
                        this.fields.put(columnName, getFieldValue(field, entity));
                    }
                } else if (field.isAnnotationPresent(JoinColumn.class)) {
                    String joinColumnName = getJoinColumnName(field);
                    if (field.getAnnotation(JoinColumn.class).updatable()) {
                        if (!isCollectionField(field) && field.getAnnotation(JoinColumn.class).fkTable().equals("")) {
                            Object joinColumnFieldValue = getFieldValue(field, entity);
                            if (joinColumnFieldValue != null) {
                                this.joinFields.put(joinColumnName, new AbstractUpdate(joinColumnFieldValue));
                                Object idColumnValue = getIdColumnValue(joinColumnName, joinColumnFieldValue.getClass(), joinColumnFieldValue);
                                if (idColumnValue != null) this.fields.put(joinColumnName, idColumnValue);
                            }
                        } else {
                            Collection<?> joinColumnFieldValue = (Collection<?>) getFieldValue(field, entity);
                            if (joinColumnFieldValue != null) {
                                List<AbstractUpdate> collectionEntities = new LinkedList<>();
                                for (Object itemInCollection : joinColumnFieldValue) {
                                    collectionEntities.add(new AbstractUpdate(itemInCollection));
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
