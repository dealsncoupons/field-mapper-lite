package works.hop.sample1.hydrate.app;

import works.hop.hydrate.jdbc.context.DbSelector;
import works.hop.hydrate.jdbc.context.SelectTemplate;
import works.hop.sample1.hydrate.entity.MyMembership;

public class MyMembershipExample {

    public static void main(String[] args) {
        String queryByAlias = "select m.* from tbl_membership m where m.member_alias = ?";
        MyMembership membership = SelectTemplate.selectOne(new MyMembership(), queryByAlias, DbSelector.selector(), new Object[]{"fitness-jamesbrown"});
        System.out.println(membership);
    }
}
