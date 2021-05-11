package works.hop.javro.jdbc;

import works.hop.javro.jdbc.sample.FieldInfo;

import java.util.List;

public class EntityInfo {

    private String tableName;
    private List<FieldInfo> fields;

    public EntityInfo() {
    }

    public EntityInfo(String tableName, List<FieldInfo> fields) {
        this.tableName = tableName;
        this.fields = fields;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<FieldInfo> getFields() {
        return fields;
    }

    public void setFields(List<FieldInfo> fields) {
        this.fields = fields;
    }
}
