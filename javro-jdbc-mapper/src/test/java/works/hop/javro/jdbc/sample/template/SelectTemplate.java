package works.hop.javro.jdbc.sample.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.EntityInfo;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.FieldInfo;
import works.hop.javro.jdbc.sample.MapResultSetToEntity;
import works.hop.javro.jdbc.template.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SelectTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    private static String prepareQuery(EntityInfo entityInfo) {
        List<String> fields = allColumns(entityInfo.getFields());
        List<String> idFields = idColumns(entityInfo.getFields());
        StringBuilder builder = new StringBuilder();
        builder.append("select ");
        int fieldIndex = 0;
        for (String columnName : fields) {
            builder.append(columnName);
            fieldIndex++;
            if (fieldIndex < fields.size()) {
                builder.append(", ");
            }
        }
        builder.append(" from ").append(entityInfo.getTableName()).append(" where ");
        int idColumnIndex = 0;
        for (String idColumn : idFields) {
            builder.append(idColumn).append(" = ?::uuid");
            idColumnIndex += 1;
            if (idColumnIndex < idFields.size()) {
                builder.append(" and ");
            }
        }
        return builder.toString();
    }

    private static List<String> allColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> !field.isCollection)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    private static List<String> idColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    public static <E> E selectOne(Class<E> type, Object[] args) {
        EntityInfo entityInfo = EntityMetadata.getEntityInfo.apply(type);
        String query = prepareQuery(entityInfo);
        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    MapResultSetToEntity mapper = new MapResultSetToEntity();
                    return mapper.mapRsToEntity(rs, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }

    public static <E> Collection<E> selectList(Class<E> type, Object[] args) {
        EntityInfo entityInfo = EntityMetadata.getEntityInfo.apply(type);
        String query = prepareQuery(entityInfo);
        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    MapResultSetToEntity mapper = new MapResultSetToEntity();
                    return mapper.mapRsToEntityCollection(rs, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }
}
