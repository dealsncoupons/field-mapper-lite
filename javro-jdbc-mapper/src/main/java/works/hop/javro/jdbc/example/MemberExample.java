package works.hop.javro.jdbc.example;

import works.hop.javro.jdbc.entity.Address;
import works.hop.javro.jdbc.entity.Member;
import works.hop.javro.jdbc.repository.MemberRepo;
import works.hop.javro.jdbc.template.CrudRepoFactory;

import java.time.LocalDate;
import java.util.Optional;

public class MemberExample {

    public static void main(String[] args) {
//        creatingMember();
        fetchingMember();
    }

    public static void creatingMember() {
        MemberRepo memberRepo = CrudRepoFactory.getInstance(MemberRepo.class);

        Member member = new Member();
        member.fullName = "jason, dish";
        member.emailAddr = "jasondish@email.com";
        Address londonAddr = new Address();
        londonAddr.state = "East London";
        londonAddr.city = "London";
        member.address = londonAddr;
        member.dateCreated = LocalDate.now();
        //save
        Member saved = memberRepo.save(member);
        System.out.println(saved);

        Member newMember = new Member();
        newMember.emailAddr = "jackwish1@email.com";
        Address madisonAddr = new Address();
        madisonAddr.city = "Madison";
        madisonAddr.state = "WI";
        newMember.address = madisonAddr;
        newMember.dateCreated = LocalDate.now();
        newMember.fullName = "Wish, Jack1";
        //save
        Member savedMember = memberRepo.save(newMember);
        System.out.println(savedMember);
    }

    public static void fetchingMember() {
        MemberRepo memberRepo = CrudRepoFactory.getInstance(MemberRepo.class);
        Member byEmail = memberRepo.findByEmail("bigben@email.com");
        System.out.println(byEmail);

        Optional<Member> byId = memberRepo.findById(byEmail.id);
        byId.ifPresent(System.out::println);
    }
}
