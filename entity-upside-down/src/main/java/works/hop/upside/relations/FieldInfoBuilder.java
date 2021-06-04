package works.hop.upside.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FieldInfoBuilder {

    public Boolean isId = Boolean.FALSE;
    public Class<?> type = String.class;
    public String name;
    public String columnName;
    public Boolean isEmbedded = Boolean.FALSE;
    public Boolean isRelational = Boolean.FALSE;
    public Boolean isCollection = Boolean.FALSE;
    public Boolean isUpdatable = Boolean.TRUE;
    public Boolean isTemporal = Boolean.FALSE;
    public String joinTable;
    public List<FieldInfo> embeddedFields = new ArrayList<>();

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

    public FieldInfoBuilder updatable(Boolean updatable) {
        isUpdatable = updatable;
        return this;
    }

    public FieldInfoBuilder temporal(Boolean temporal) {
        isTemporal = temporal;
        return this;
    }

    public FieldInfoBuilder joinTable(String joinTable) {
        this.joinTable = joinTable;
        return this;
    }

    public FieldInfoBuilder embeddedField(Function<FieldInfoBuilder, FieldInfo> builder) {
        this.embeddedFields.add(builder.apply(builder()));
        return this;
    }

    public FieldInfo build() {
        this.columnName = (this.columnName == null && !this.isCollection) ? this.name : this.columnName;
        this.isRelational = this.isCollection || this.isRelational;
        return new FieldInfo(
                this.isId,
                this.type,
                this.name,
                this.columnName,
                this.isEmbedded,
                this.isRelational,
                this.isCollection,
                this.isUpdatable,
                this.isTemporal,
                this.joinTable, this.embeddedFields);
    }
}
