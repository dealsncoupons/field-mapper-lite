package works.hop.hydrate.jdbc.context;

import works.hop.hydrate.jdbc.relations.EntityQuery;

import java.sql.Connection;
import java.util.Collection;
import java.util.function.Supplier;

public class DbSelector {

    private static final Object lock = new Object();
    private static DbSelector selector;

    private final EntityQuery entityQuery = EntityQuery.getInstance();

    private DbSelector(){}

    public static DbSelector selector(){
        synchronized (lock){
            if(selector == null){
                selector = new DbSelector();
            }
            return selector;
        }
    }

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

    public <T extends Hydrate> Collection<T> selectByRange(Supplier<T> newInstance, String srcTable, int limit, int offset) {
        String query = entityQuery.selectRange(srcTable);
        return SelectTemplate.selectList(newInstance, query, this, new Object[]{limit, offset});
    }
}
