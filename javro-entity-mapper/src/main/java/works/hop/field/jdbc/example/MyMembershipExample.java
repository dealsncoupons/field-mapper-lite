package works.hop.field.jdbc.example;

import works.hop.field.jdbc.dto.MyMembership;
import works.hop.field.jdbc.repository.MembershipRepo;

public class MyMembershipExample {

    public static void main(String[] args) {
        MembershipRepo membershipRepo = new MembershipRepo();
        MyMembership membership = membershipRepo.findMyMemberships("fitness-jamesbrown");
        System.out.println(membership);
    }
}
