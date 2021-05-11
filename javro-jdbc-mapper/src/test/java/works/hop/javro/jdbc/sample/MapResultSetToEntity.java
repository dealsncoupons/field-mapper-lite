package works.hop.javro.jdbc.sample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapResultSetToEntity<T> {

    public T mapRsToEntity(ResultSet rs, Class<T> type) throws SQLException {
        Map<String, Object> source = new HashMap<>();

        List<FieldInfo> entityFields = EntityInfo.getEntityInfo.apply(type);
        int columnCount = rs.getMetaData().getColumnCount();
        if (rs.next()) {
            for (int i = 0; i < columnCount; i++) {
                String columnName = rs.getMetaData().getColumnName(i);
                Optional<FieldInfo> fieldInfo = entityFields.stream()
                        .filter(field -> field.columnName.equals(columnName))
                        .findFirst();

                if (fieldInfo.isPresent()) {
                    FieldInfo field = fieldInfo.get();
                    if (field.isCollection) {
                        if (field.joinColumn != null && field.joinColumn.trim().length() > 0) {
                            System.out.println("Not yet handled");
                        }
                        else{
                            System.out.println("Not yet handled");
                        }
                    } else if (field.isEmbedded) {
                        System.out.println("Not yet handled");
                    } else {
                        Object columnValue = rs.getObject(columnName, field.type);
                        source.put(field.name, columnValue);
                    }
                }
            }
        }
        return EntityProxyFactory.create(type, source);
    }
}
