package works.hop.field.jdbc.example;

import works.hop.field.jdbc.entity.Address;
import works.hop.field.jdbc.entity.Member;
import works.hop.field.jdbc.repository.MemberRepo;

import java.time.LocalDate;

public class MemberExample {

    public static void main(String[] args) {
        creatingMember();
    }

    public static void creatingMember() {
        MemberRepo memberRepo = new MemberRepo();

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

    public void fetchingMember() {
        MemberRepo memberRepo = new MemberRepo();
        Member byEmail = memberRepo.findByEmail("bigben@email.com");
        System.out.println(byEmail);

        Member byId = memberRepo.findById(byEmail.id);
        System.out.println(byId);
    }
}
