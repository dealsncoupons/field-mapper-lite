package works.hop.javro.jdbc.example;

import works.hop.javro.jdbc.dto.MyMembership;
import works.hop.javro.jdbc.repository.MyMembershipRepo;
import works.hop.javro.jdbc.template.CrudRepoFactory;

public class MyMembershipExample {

    public static void main(String[] args) {
        MyMembershipRepo membershipRepo = CrudRepoFactory.getInstance(MyMembershipRepo.class);
        MyMembership membership = membershipRepo.findMyMemberships("fitness-jamesbrown");
        System.out.println(membership);
    }
}
