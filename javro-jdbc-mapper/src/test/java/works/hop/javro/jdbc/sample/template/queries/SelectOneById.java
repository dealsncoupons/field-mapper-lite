package works.hop.javro.jdbc.sample.template.queries;

import works.hop.javro.jdbc.sample.EntityInfo;

import java.util.List;
import java.util.function.Function;

import static works.hop.javro.jdbc.sample.template.StaticQueries.allColumns;
import static works.hop.javro.jdbc.sample.template.StaticQueries.idColumns;

public interface SelectOneById {

    static Function<EntityInfo, String> get() {
        return (EntityInfo entityInfo) -> {
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
        };
    }
}
