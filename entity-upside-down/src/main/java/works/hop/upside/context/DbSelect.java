package works.hop.upside.context;

import works.hop.upside.relations.EntityQuery;

import java.sql.Connection;
import java.util.Collection;

public class DbSelect {

    private final EntityQuery entityQuery = EntityQuery.getInstance();

    public <T extends Hydrant, ID> T selectByIdColumn(String srcTable, String pkColumn, ID pkValue, Class<T> returnType) {
        String query = entityQuery.oneToOne(srcTable, pkColumn);
        return SelectTemplate.selectOne(query, returnType, this, new Object[]{pkValue});
    }

    public <T extends Hydrant, ID> T selectByIdColumn(String srcTable, String pkColumn, ID pkValue, Class<T> returnType, Connection conn) {
        String query = entityQuery.oneToOne(srcTable, pkColumn);
        return SelectTemplate.selectOne(query, returnType, this, new Object[]{pkValue}, conn);
    }

    public <T extends Hydrant, ID> Collection<T> selectByJoinColumn(String srcTable, String pkColumn, String joinTable, String joinColumn, String whereColumn, ID joinValue, Class<T> returnType) {
        String query = entityQuery.manyToOne(srcTable, pkColumn, joinTable, joinColumn, whereColumn);
        return SelectTemplate.selectList(query, returnType, this, new Object[]{joinValue});
    }

    public <T extends Hydrant, ID> Collection<T> selectByJoinColumn(String srcTable, String pkColumn, String joinTable, String joinColumn, String whereColumn, ID joinValue, Class<T> returnType, Connection conn) {
        String query = entityQuery.manyToOne(srcTable, pkColumn, joinTable, joinColumn, whereColumn);
        return SelectTemplate.selectList(query, returnType, this, new Object[]{joinValue}, conn);
    }
}
