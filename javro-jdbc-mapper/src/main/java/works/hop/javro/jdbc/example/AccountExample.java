package works.hop.javro.jdbc.example;

import works.hop.javro.jdbc.entity.Account;
import works.hop.javro.jdbc.entity.Address;
import works.hop.javro.jdbc.entity.Member;
import works.hop.javro.jdbc.repository.AccountRepo;
import works.hop.javro.jdbc.template.CrudRepoFactory;

import java.time.LocalDate;
import java.util.Optional;

public class AccountExample {

    public static void main(String[] args) {
        AccountRepo accountRepo = CrudRepoFactory.getInstance(AccountRepo.class);
        Optional<Account> account = accountRepo.findByUnique("username", "bigben");
        account.ifPresent(found -> {
            System.out.println(found);
            Optional<Account> account1 = accountRepo.findById(found.id);
            account1.ifPresent(System.out::println);
        });
        createAccountWithMember(accountRepo);
    }

    private static void createAccountWithMember(AccountRepo accountRepo) {
        Member newMember = new Member();
        Address newAddress = new Address();
        newAddress.city = "Los Angeles";
        newAddress.state = "CA";
        newAddress.zipCode = "90201";
        newMember.address = newAddress;
        newMember.dateCreated = LocalDate.now();
        newMember.emailAddr = "new_member@email.com";
        newMember.fullName = "Weza, Makena";
        Account newAccount = new Account();
        newAccount.member = newMember;
        newAccount.accessCode = "access-123";
        newAccount.dateCreated = LocalDate.now();
        newAccount.username = "superman";
        //save member together with account info
        Account savedAccount = accountRepo.save(newAccount);
        System.out.println("Created account id - " + savedAccount.id);
        System.out.println("Created member id - " + savedAccount.member.id);

        //update both account and member
        savedAccount.member.address.city = "Chicago";
        savedAccount.member.address.state = "IL";
        savedAccount.member.address.zipCode = "60606";

        savedAccount.accessCode = "abc-xyz";
        savedAccount.username = "washa";
        accountRepo.update(savedAccount);

        //retrieve updated account
        Optional<Account> updatedAccount = accountRepo.findById(savedAccount.id);
        updatedAccount.ifPresent(account -> System.out.println("Created account id - " + account.username));

        //delete account record
        Account deletedAccount = accountRepo.deleteById(savedAccount.id);
        System.out.println("Created account id - " + deletedAccount.id);
        System.out.println("Created member id - " + deletedAccount.member.id);
    }
}
