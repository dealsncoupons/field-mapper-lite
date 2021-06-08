package works.hop.sample1.hydrate.app;

import works.hop.hydrate.jdbc.context.DbSelector;
import works.hop.hydrate.jdbc.context.SelectTemplate;
import works.hop.sample1.hydrate.entity.Membership;

import java.util.Collection;

public class MembershipExample {

    public static void main(String[] args) {
        fetchingMembership();
    }

    public static void fetchingMembership() {
        String queryByAlias = "select m.* from tbl_membership m where m.member_alias = ?";
        Membership membership = SelectTemplate.selectOne(new Membership(), queryByAlias, DbSelector.selector(), new Object[]{"fitness-jamesbrown"});
        System.out.println(membership);

        String queryByMember = "select m.* from tbl_membership m where m.member_id = ?";
        Collection<Membership> memberships = SelectTemplate.selectList(Membership::new, queryByMember, DbSelector.selector(), new Object[]{membership.getMember()});
        System.out.println(memberships);
    }
}
