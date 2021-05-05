package works.hop.field.jdbc.repository;

import works.hop.field.jdbc.entity.Account;
import works.hop.field.jdbc.resolver.AbstractResolver;
import works.hop.field.jdbc.template.FetchersFactory;
import works.hop.field.jdbc.template.SelectTemplate;

import java.util.UUID;

public class AccountRepo {

    //    @Query("select * from tbl_account where id = ?::uuid")
    public Account findById(UUID id) {
        String query = "select * from tbl_account where id = ?::uuid";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<Account> entityFetcher = FetchersFactory.entityFetcher(Account.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{id});
    }

    public Account findByUsername(String username) {
        String query = "select * from tbl_account where username = ?";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<Account> entityFetcher = FetchersFactory.entityFetcher(Account.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{username});
    }
}
