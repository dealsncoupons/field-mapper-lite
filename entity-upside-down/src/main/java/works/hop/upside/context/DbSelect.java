package works.hop.upside.context;

import works.hop.upside.relations.EntityQuery;

import java.sql.Connection;
import java.util.Collection;
import java.util.function.Supplier;

public class DbSelect {

    private final EntityQuery entityQuery = EntityQuery.getInstance();

    public <T extends Hydrate, ID> T selectByIdColumn(T entity, String srcTable, String pkColumn, ID pkValue) {
        String query = entityQuery.oneToOne(srcTable, pkColumn);
        return SelectTemplate.selectOne(entity, query, this, new Object[]{pkValue});
    }

    public <T extends Hydrate, ID> T selectByIdColumn(T entity, String srcTable, String pkColumn, ID pkValue, Connection conn) {
        String query = entityQuery.oneToOne(srcTable, pkColumn);
        return SelectTemplate.selectOne(entity, query, this, new Object[]{pkValue}, conn);
    }

    public <T extends Hydrate, ID> Collection<T> selectByJoinColumn(Supplier<T> newInstance, String srcTable, String pkColumn, String joinTable, String joinColumn, String whereColumn, ID joinValue) {
        String query = entityQuery.manyToOne(srcTable, pkColumn, joinTable, joinColumn, whereColumn);
        return SelectTemplate.selectList(newInstance, query, this, new Object[]{joinValue});
    }

    public <T extends Hydrate, ID> Collection<T> selectByJoinColumn(Supplier<T> newInstance, String srcTable, String pkColumn, String joinTable, String joinColumn, String whereColumn, ID joinValue, Connection conn) {
        String query = entityQuery.manyToOne(srcTable, pkColumn, joinTable, joinColumn, whereColumn);
        return SelectTemplate.selectList(newInstance, query, this, new Object[]{joinValue}, conn);
    }
}
