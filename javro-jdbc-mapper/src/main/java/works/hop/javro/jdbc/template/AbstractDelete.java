package works.hop.javro.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.annotation.Embedded;
import works.hop.javro.jdbc.annotation.Id;
import works.hop.javro.jdbc.annotation.JoinColumn;
import works.hop.javro.jdbc.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.*;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.*;

public class AbstractDelete {

    static final Logger log = LoggerFactory.getLogger(AbstractDelete.class);
    public final Map<String, Object> idFields = new LinkedHashMap<>();
    public final Map<String, AbstractDelete> joinFields = new LinkedHashMap<>();
    public final Map<String, List<AbstractDelete>> collectionJoinFields = new LinkedHashMap<>();

    public AbstractDelete(Object entity) {
        Class<?> targetClass = entity.getClass();
        do {
            Field[] fields = targetClass.getDeclaredFields();
            Arrays.stream(fields).filter(ReflectionUtil::isAcceptableField).forEach(field -> {
                if (field.isAnnotationPresent(Id.class)) {
                    String columnName = getColumnName(field);
                    this.idFields.put(columnName, getFieldValue(field, entity));
                } else if (field.isAnnotationPresent(JoinColumn.class)) {
                    String joinColumnName = getJoinColumnName(field);
                    if (field.getAnnotation(JoinColumn.class).updatable()) {
                        if (!isCollectionField(field) && field.getAnnotation(JoinColumn.class).fkTable().equals("")) {
                            Object joinColumnFieldValue = getFieldValue(field, entity);
                            if (joinColumnFieldValue != null) {
                                this.joinFields.put(joinColumnName, new AbstractDelete(joinColumnFieldValue));
                            }
                        } else {
                            Collection<?> joinColumnFieldValue = (Collection<?>) getFieldValue(field, entity);
                            if (joinColumnFieldValue != null) {
                                List<AbstractDelete> collectionEntities = new LinkedList<>();
                                for (Object itemInCollection : joinColumnFieldValue) {
                                    collectionEntities.add(new AbstractDelete(itemInCollection));
                                }
                                this.collectionJoinFields.put(joinColumnName, collectionEntities);
                            }
                        }
                    }
                }
            });
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != Object.class);
    }
}
