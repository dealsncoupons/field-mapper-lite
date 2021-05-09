package works.hop.javro.jdbc.example;

import works.hop.javro.jdbc.entity.Membership;
import works.hop.javro.jdbc.repository.MembershipRepo;
import works.hop.javro.jdbc.template.CrudRepoFactory;

import java.util.List;

public class MembershipExample {

    public static void main(String[] args) {
        fetchingMembership();
    }

    public static void fetchingMembership() {
        MembershipRepo membershipRepo = CrudRepoFactory.getInstance(MembershipRepo.class);
        Membership membership = membershipRepo.findByMemberAlias("fitness-jamesbrown");
        System.out.println(membership);

        Membership membership1 = membershipRepo.findById(membership.member, membership.club);
        System.out.println(membership1);

        List<Membership> memberships = membershipRepo.findByMemberId(membership.member);
        System.out.println(memberships);
    }
}
