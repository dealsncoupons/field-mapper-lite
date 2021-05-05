package works.hop.field.jdbc.example;

import works.hop.field.jdbc.entity.Membership;
import works.hop.field.jdbc.repository.MembershipRepo;

import java.util.List;

public class MembershipExample {

    public static void main(String[] args) {
        fetchingMembership();
    }

    public static void fetchingMembership() {
        MembershipRepo membershipRepo = new MembershipRepo();
        Membership membership = membershipRepo.findByMemberAlias("fitness-jamesbrown");
        System.out.println(membership);

        Membership membership1 = membershipRepo.findById(membership.member, membership.club);
        System.out.println(membership1);

        List<Membership> memberships = membershipRepo.findByMemberId(membership.member);
        System.out.println(memberships);
    }
}
