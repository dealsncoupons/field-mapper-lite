package works.hop.upside.relations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EntityQuery {

    private static final Object lock = new Object();
    private static EntityQuery instance;

    private EntityQuery() {
    }

    public static EntityQuery getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new EntityQuery();
            }
        }
        return instance;
    }

    public String oneToOne(String srcTable, String whereColumn) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("srcTable", srcTable);
        replacements.put("srcTableAlias", "SRC_TBL");
        replacements.put("whereColumn", whereColumn);

        String queryTemplate = "select :srcTableAlias.* from :srcTable :srcTableAlias where :srcTableAlias.:whereColumn = ?";

        return Pattern.compile(":(\\w+)").matcher(queryTemplate).replaceAll(match -> replacements.get(match.group(1)));
    }

    public String manyToOne(String srcTable, String pkColumn, String joinTable, String joinColumn, String whereColumn) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("srcTable", srcTable);
        replacements.put("srcTableAlias", "SRC_TBL");
        replacements.put("pkColumn", pkColumn);
        replacements.put("joinTable", joinTable);
        replacements.put("joinTableAlias", "JOIN_TBL");
        replacements.put("joinColumn", joinColumn);
        replacements.put("whereColumn", whereColumn);

        String queryTemplate = "select :srcTableAlias.* from :srcTable :srcTableAlias " +
                "inner join :joinTable :joinTableAlias on :srcTableAlias.:pkColumn = :joinTableAlias.:joinColumn " +
                "where :srcTableAlias.:whereColumn = ?";

        return Pattern.compile(":(\\w+)").matcher(queryTemplate).replaceAll(match -> replacements.get(match.group(1)));
    }

    public String insertOne(String tableName, String[] tableColumns) {
        String columns = String.join(", ", tableColumns);
        String values = Arrays.stream(columns.split(", ")).map(f -> "?").collect(Collectors.joining(", "));
        return String.join(" ", "insert into", tableName, "(", columns, ") values (", values, ") on conflict do nothing returning id::uuid");
    }

    public String updateOne(String tableName, String[] idColumns, String[] valuesColumns) {
        String setValues = Arrays.stream(valuesColumns).map(column -> String.format("%s = ?", column)).collect(Collectors.joining(", "));
        String idValues = Arrays.stream(idColumns).map(column -> String.format("%s = ?", column)).collect(Collectors.joining(" and "));
        return String.join(" ", "update", tableName, "set", setValues, "where", idValues);
    }

    public String deleteOne(String tableName, String[] idColumns) {
        String idValues = Arrays.stream(idColumns).map(column -> String.format("%s = ?", column)).collect(Collectors.joining(" and "));
        return String.join(" ", "delete from", tableName, "where", idValues);
    }
}
