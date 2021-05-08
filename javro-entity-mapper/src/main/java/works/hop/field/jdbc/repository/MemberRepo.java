package works.hop.field.jdbc.repository;

import works.hop.field.jdbc.entity.Member;
import works.hop.field.jdbc.resolver.AbstractResolver;
import works.hop.field.jdbc.template.FetchersFactory;
import works.hop.field.jdbc.template.InsertTemplate;
import works.hop.field.jdbc.template.SelectTemplate;

import java.util.UUID;

public class MemberRepo {

    public Member findById(UUID id) {
        String query = "select * from tbl_member where id = ?::uuid";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<Member> entityFetcher = FetchersFactory.entityFetcher(Member.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{id});
    }

    public Member findByEmail(String email) {
        String query = "select * from tbl_member where email_addr = ?";
        return SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<Member> entityFetcher = FetchersFactory.entityFetcher(Member.class);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{email});
    }

    public Member save(Member newMember) {
//        String query = "insert into tbl_member (full_name, email_addr, city, state_prov, date_created) values (?, ?, ?, ?, now()) " +
//                "on conflict do nothing returning id ";
        return InsertTemplate.insertOne(newMember);
    }
}
