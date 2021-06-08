package works.hop.hydrate.jdbc.relations;

import java.util.Collections;
import java.util.List;

public class FieldInfo {

    public final Boolean isId;
    public final Class<?> type;
    public final String name;
    public final String columnName;
    public final Boolean isEmbedded;
    public final Boolean isRelational;
    public final Boolean isCollection;
    public final Boolean isUpdatable;
    public final Boolean isTemporal;
    public final String joinTable;
    public final List<FieldInfo> embeddedFields;

    public FieldInfo(Class<?> type, String name, String columnName) {
        this(false, type, name, columnName, false, false, false, true, false, "", Collections.emptyList());
    }

    public FieldInfo(Boolean isId, Class<?> type, String name, String columnName) {
        this(isId, type, name, columnName, false, false, false, true, false, "", Collections.emptyList());
    }

    public FieldInfo(Class<?> type, String name, String columnName, Boolean isEmbedded, List<FieldInfo> embeddedFields) {
        this(false, type, name, columnName, isEmbedded, false, false, true, false, "", embeddedFields);
    }

    public FieldInfo(Class<?> type, String name, String columnName, Boolean isRelational, Boolean isCollection, String joinTable) {
        this(false, type, name, columnName, false, isRelational, isCollection, true, false, joinTable, Collections.emptyList());
    }

    public FieldInfo(Boolean isId, Class<?> type, String name, String columnName, Boolean isEmbedded, Boolean isRelational, Boolean isCollection, Boolean isUpdatable, Boolean isTemporal, String joinTable, List<FieldInfo> embeddedFields) {
        this.isId = isId;
        this.type = type;
        this.name = name;
        this.columnName = columnName;
        this.isEmbedded = isEmbedded;
        this.isRelational = isRelational;
        this.isCollection = isCollection;
        this.isUpdatable = isUpdatable;
        this.isTemporal = isTemporal;
        this.joinTable = joinTable;
        this.embeddedFields = embeddedFields;
    }
}
