package works.hop.javro.jdbc.sample.template;

import io.vavr.Tuple3;
import works.hop.javro.jdbc.sample.EntityInfo;
import works.hop.javro.jdbc.sample.FieldInfo;
import works.hop.javro.jdbc.sample.template.queries.SelectListByJoinColumn;
import works.hop.javro.jdbc.sample.template.queries.SelectOneById;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface StaticQueries {

    Function<EntityInfo, String> SELECT_ONE_BY_ID = SelectOneById.get();
    Function<Tuple3<EntityInfo, EntityInfo, String>, String> SELECT_LIST_BY_JOIN__COLUMN = SelectListByJoinColumn.get();

    static List<String> allColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    static List<String> idColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    static List<String> allNonIdColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    static List<FieldInfo> allFieldsExceptId(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> !field.isId)
                .collect(Collectors.toList());
    }

    static List<String> allColumnsExceptId(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> !field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }
}
