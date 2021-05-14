package works.hop.javro.jdbc.sample.template.queries;

import io.vavr.Tuple3;
import works.hop.javro.jdbc.sample.EntityInfo;
import works.hop.javro.jdbc.sample.FieldInfo;

import java.util.List;
import java.util.function.Function;

import static works.hop.javro.jdbc.sample.template.StaticQueries.*;

/**
 * select r.* from [left_table l] inner join [right_table r] on r.[id] = l.[fk_id] where
 * r.[id] = ?::uuid
 */
public interface SelectListByJoinColumn {

    static Function<Tuple3<EntityInfo, EntityInfo, String>, String> get() {
        return (tuple) -> {
            EntityInfo leftTableInfo = tuple._1;
            FieldInfo leftTablePk = leftTableInfo.getFields().stream().filter(
                    field -> field.isId
            ).findFirst().orElseThrow(() -> new RuntimeException("There's no 'id' field defined"));
            EntityInfo rightTableInfo = tuple._2;
            String rightTableFk = tuple._3;

            List<String> fields = allNonIdColumns(leftTableInfo.getFields());
            List<String> idFields = idColumns(leftTableInfo.getFields());
            StringBuilder builder = new StringBuilder();
            builder.append("select ");
            int fieldIndex = 0;
            for (String columnName : fields) {
                builder.append("l.").append(columnName);
                fieldIndex++;
                if (fieldIndex < fields.size()) {
                    builder.append(", ");
                }
            }
            builder.append(" from ").append(leftTableInfo.getTableName()).append(" l");
            builder.append(" inner join ").append(rightTableInfo.getTableName()).append(" r");
            builder.append(" on r.").append(rightTableFk).append(" = l.");
            builder.append(leftTablePk.columnName).append(" where ");
            int idColumnIndex = 0;
            for (String idColumn : idFields) {
                builder.append("l.").append(idColumn).append(" = ?::uuid");
                idColumnIndex += 1;
                if (idColumnIndex < idFields.size()) {
                    builder.append(" and ");
                }
            }
            return builder.toString();
        };
    }
}
