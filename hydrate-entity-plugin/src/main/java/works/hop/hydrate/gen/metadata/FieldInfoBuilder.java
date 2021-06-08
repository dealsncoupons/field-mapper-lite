package works.hop.hydrate.gen.metadata;

public class FieldInfoBuilder {

    public Boolean isId = Boolean.FALSE;
    public Class<?> type;
    public String name;
    public String columnName;
    public Boolean isEmbedded = Boolean.FALSE;
    public Boolean isRelational = Boolean.FALSE;
    public Boolean isCollection = Boolean.FALSE;
    public String joinTable;

    private FieldInfoBuilder() {
    }

    public static FieldInfoBuilder builder() {
        return new FieldInfoBuilder();
    }

    public FieldInfoBuilder isId(Boolean isId) {
        this.isId = isId;
        return this;
    }

    public FieldInfoBuilder type(Class<?> type) {
        this.type = type;
        return this;
    }

    public FieldInfoBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FieldInfoBuilder columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public FieldInfoBuilder embedded(Boolean embedded) {
        isEmbedded = embedded;
        return this;
    }

    public FieldInfoBuilder relational(Boolean relational) {
        isRelational = relational;
        return this;
    }

    public FieldInfoBuilder collection(Boolean collection) {
        isCollection = collection;
        return this;
    }

    public FieldInfoBuilder joinTable(String joinTable) {
        this.joinTable = joinTable;
        return this;
    }

    public FieldInfo build() {
        return new FieldInfo(
                this.isId,
                this.type,
                this.name,
                this.columnName,
                this.isEmbedded,
                this.isRelational,
                this.isCollection,
                this.joinTable);
    }
}
