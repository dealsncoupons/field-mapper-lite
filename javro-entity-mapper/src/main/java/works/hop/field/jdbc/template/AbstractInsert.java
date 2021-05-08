package works.hop.field.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.field.jdbc.annotation.Column;
import works.hop.field.jdbc.annotation.Embedded;
import works.hop.field.jdbc.annotation.JoinColumn;
import works.hop.field.jdbc.resolver.AbstractResolver;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static works.hop.field.jdbc.resolver.AbstractResolver.*;

public class AbstractInsert {

    static final Logger log = LoggerFactory.getLogger(AbstractInsert.class);
    public final Map<String, Object> fields = new HashMap<>();

    public AbstractInsert(Object entity) {
        Class<?> targetClass = entity.getClass();
        do {
            Field[] fields = targetClass.getDeclaredFields();
            Arrays.stream(fields).filter(AbstractResolver::isAcceptableField).forEach(field -> {
                if (field.isAnnotationPresent(Embedded.class)) {
                    String embeddedFieldName = getColumnName(field);
                    Object embeddedFieldValue = getFieldValue(field, entity);
                    if (embeddedFieldValue != null) {
                        this.fields.putAll(new AbstractInsert(embeddedFieldValue).fields);
                        log.info("Added embedded field '{}' in entity object", embeddedFieldName);
                    }
                } else if (field.isAnnotationPresent(Column.class)) {
                    String columnName = getColumnName(field);
                    this.fields.put(columnName, getFieldValue(field, entity));
                } else if (field.isAnnotationPresent(JoinColumn.class)) {
                    String joinColumnName = getJoinColumnName(field);
                    if (isCollectionField(field)) {
                        List<Object> listValues = ((List<Object>) getFieldValue(field, entity)).stream().map(listValue ->
                                new AbstractInsert(listValue).fields).collect(Collectors.toList());
                        this.fields.put(joinColumnName, listValues);
                    } else {
                        Object joinColumnFieldValue = getFieldValue(field, entity);
                        if (joinColumnFieldValue != null) {
                            this.fields.put(joinColumnName, new AbstractInsert(joinColumnFieldValue).fields);
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
