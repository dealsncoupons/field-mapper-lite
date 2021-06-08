package works.hop.hydrate.gen.metadata;

public class FieldInfo {

    public final Boolean isId;
    public final Class<?> type;
    public final String name;
    public final String columnName;
    public final Boolean isEmbedded;
    public final Boolean isRelational;
    public final Boolean isCollection;
    public final String joinTable;

    public FieldInfo(Class<?> type, String name, String columnName) {
        this(false, type, name, columnName, false, false, false, "");
    }

    public FieldInfo(Boolean isId, Class<?> type, String name, String columnName) {
        this(isId, type, name, columnName, false, false, false, "");
    }

    public FieldInfo(Class<?> type, String name, String columnName, Boolean isEmbedded) {
        this(false, type, name, columnName, isEmbedded, false, false, "");
    }

    public FieldInfo(Class<?> type, String name, String columnName, Boolean isRelational, Boolean isCollection, String joinTable) {
        this(false, type, name, columnName, false, isRelational, isCollection, joinTable);
    }

    public FieldInfo(Boolean isId, Class<?> type, String name, String columnName, Boolean isEmbedded, Boolean isRelational, Boolean isCollection, String joinTable) {
        this.isId = isId;
        this.type = type;
        this.name = name;
        this.columnName = columnName;
        this.isEmbedded = isEmbedded;
        this.isRelational = isRelational;
        this.isCollection = isCollection;
        this.joinTable = joinTable;
    }
}
