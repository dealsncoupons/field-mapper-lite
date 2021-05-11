package works.hop.javro.jdbc.sample;

public class FieldInfo {

    public final Class<?> type;
    public final String name;
    public final String columnName;
    public final Boolean isEmbedded;
    public final Boolean isRelational;
    public final Boolean isCollection;
    public final String joinColumn;

    public FieldInfo(Class<?> type, String name, String columnName, Boolean isEmbedded, Boolean isRelational, Boolean isCollection, String joinColumn) {
        this.type = type;
        this.name = name;
        this.columnName = columnName;
        this.isEmbedded = isEmbedded;
        this.isRelational = isRelational;
        this.isCollection = isCollection;
        this.joinColumn = joinColumn;
    }
}
