package works.hop.sample1.hydrate.app;

import works.hop.hydrate.jdbc.context.DbSelector;
import works.hop.hydrate.jdbc.context.InsertTemplate;
import works.hop.hydrate.jdbc.context.SelectTemplate;
import works.hop.sample1.hydrate.entity.Address;
import works.hop.sample1.hydrate.entity.Member;
import works.hop.sample1.hydrate.entity.builder.AddressBuilder;
import works.hop.sample1.hydrate.entity.builder.MemberBuilder;

import java.time.LocalDate;

public class MemberExample {

    public static void main(String[] args) {
//        creatingMember();
        fetchingMember();
    }

    public static void creatingMember() {
        Address londonAddress = AddressBuilder.newBuilder()
                .city("London")
                .state("East London").build();
        Member londonMember = MemberBuilder.newBuilder()
                .fullName("jason, dish")
                .emailAddr("jasondish@email.com")
                .dateCreated(LocalDate.now())
                .address(londonAddress).build();

        //save
        Member saved = InsertTemplate.insertOne(londonMember);
        System.out.println(saved);

        Address madisonAddress = AddressBuilder.newBuilder()
                .city("Madison")
                .state("WI").build();
        Member madisonMember = MemberBuilder.newBuilder()
                .emailAddr("jackwish1@email.com")
                .address(madisonAddress)
                .fullName("Wish, Jack1")
                .dateCreated(LocalDate.now()).build();
        //save
        Member savedMember = InsertTemplate.insertOne(madisonMember);
        System.out.println(savedMember);
    }

    public static void fetchingMember() {
        String queryByEmail = "select m.* from tbl_member m where m.email_addr = ?";
        Member byEmail = SelectTemplate.selectOne(new Member(), queryByEmail, DbSelector.selector(), new Object[]{"bigben@email.com"});
        System.out.println(byEmail);

        String queryById = "select m.* from tbl_member m where m.id = ?";
        Member byId = SelectTemplate.selectOne(new Member(), queryById, DbSelector.selector(), new Object[]{byEmail.getId()});
        System.out.println(byId);
    }
}
