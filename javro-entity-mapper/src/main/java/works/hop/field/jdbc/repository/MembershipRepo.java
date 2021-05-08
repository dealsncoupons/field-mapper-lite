package works.hop.field.jdbc.repository;

import works.hop.field.jdbc.dto.MyMembership;
import works.hop.field.jdbc.entity.Membership;
import works.hop.field.jdbc.resolver.AbstractResolver;
import works.hop.field.jdbc.template.FetchersFactory;
import works.hop.field.jdbc.template.SelectTemplate;

import java.util.List;
import java.util.UUID;

public class MembershipRepo {

    //    @Query("select * from tbl_account where id = ?::uuid")
    public Membership findById(UUID member, UUID club) {
        String query = "select * from tbl_membership where member_id = ?::uuid and club_id = ?::uuid";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<Membership> entityFetcher = FetchersFactory.entityFetcher(Membership.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{member, club});
    }

    public Membership findByMemberAlias(String alias) {
        String query = "select * from tbl_membership where member_alias = ?";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<Membership> entityFetcher = FetchersFactory.entityFetcher(Membership.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{alias});
    }

    public List<Membership> findByMemberId(UUID member) {
        String query = "select * from tbl_membership where member_id = ?::uuid";
        return SelectTemplate.selectList(query, rs -> {
            AbstractResolver<Membership> entityFetcher = FetchersFactory.entityFetcher(Membership.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{member});
    }

    public MyMembership findMyMemberships(String alias) { //criteria must be unique in this use-case
        String query = "select * from tbl_membership where member_alias = ?";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<MyMembership> entityFetcher = FetchersFactory.entityFetcher(MyMembership.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{alias});
    }
}
