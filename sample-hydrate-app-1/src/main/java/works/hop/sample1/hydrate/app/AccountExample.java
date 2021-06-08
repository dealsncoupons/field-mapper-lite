package works.hop.sample1.hydrate.app;

import works.hop.hydrate.jdbc.context.*;
import works.hop.sample1.hydrate.entity.Account;
import works.hop.sample1.hydrate.entity.Address;
import works.hop.sample1.hydrate.entity.Member;
import works.hop.sample1.hydrate.entity.builder.AccountBuilder;
import works.hop.sample1.hydrate.entity.builder.AddressBuilder;
import works.hop.sample1.hydrate.entity.builder.MemberBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AccountExample {

    public static void main(String[] args) {
        String queryByUsername = "select a.* from tbl_account a where a.username = ?";
        Account account = SelectTemplate.selectOne(new Account(), queryByUsername, DbSelector.selector(), new Object[]{"bigben"});
        if (account != null) {
            System.out.println(account);
        }
        createAccountWithMember();
    }

    private static void createAccountWithMember() {
        Address address = AddressBuilder.newBuilder()
                .city("Los Angeles")
                .state("CA")
                .zipCode("90201").build();

        Member newMember = MemberBuilder.newBuilder()
                .address(address)
                .dateCreated(LocalDate.now())
                .emailAddr("new_member@email.com")
                .fullName("Weza, Makena").build();

        Account account = AccountBuilder.newBuilder()
                .member(newMember)
                .dateCreated(LocalDate.now())
                .username("superman")
                .accessCode("access-123")
                .build();

        //save member together with account info
        Account savedAccount = InsertTemplate.insertOne(account);
        System.out.println("Created account id - " + savedAccount.getId());
        System.out.println("Created member id - " + savedAccount.getMember().getId());

        //update both account and member
        Member accountMember = savedAccount.getMember();
        Map<String, Object> addressUpdates = new HashMap<>();
        addressUpdates.put("city", "Chicago");
        addressUpdates.put("state", "IL");
        addressUpdates.put("zipCode", "60606");

        Map<String, Object> memberUpdates = new HashMap<>();
        memberUpdates.put("address", addressUpdates);

        Map<String, Object> accountUpdates = new HashMap<>();
        accountUpdates.put("accessCode", "abc-xyz");
        accountUpdates.put("username", "washa");
        accountUpdates.put("member", memberUpdates);

        savedAccount = UpdateTemplate.updateOne(savedAccount, accountUpdates);

        //retrieve updated account
        String queryById = "select a.* from tbl_account a where a.id = ?::uuid";
        Account updatedAccount = SelectTemplate.selectOne(new Account(), queryById, DbSelector.selector(), new Object[]{savedAccount.getId()});
        if (updatedAccount != null) {
            System.out.println("Updated account id - " + updatedAccount.getId());
        }

        //delete account record
        Account deletedAccount = DeleteTemplate.deleteOne(updatedAccount);
        System.out.println("Deleted account id - " + deletedAccount.getId());
    }
}
