package works.hop.field.jdbc.repository;

import works.hop.field.jdbc.entity.Club;
import works.hop.field.jdbc.resolver.AbstractResolver;
import works.hop.field.jdbc.template.FetchersFactory;
import works.hop.field.jdbc.template.SelectTemplate;

import java.util.UUID;

public class ClubRepo {

    //    @Query("select * from tbl_account where id = ?::uuid")
    public Club findById(UUID id) {
        String query = "select * from tbl_club where id = ?::uuid";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<Club> entityFetcher = FetchersFactory.entityFetcher(Club.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{id});
    }

    public Club findByTitle(String title) {
        String query = "select * from tbl_club where title = ?";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<Club> entityFetcher = FetchersFactory.entityFetcher(Club.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{title});
    }
}
