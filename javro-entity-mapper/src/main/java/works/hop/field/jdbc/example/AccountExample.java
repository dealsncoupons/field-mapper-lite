package works.hop.field.jdbc.example;

import works.hop.field.jdbc.entity.Account;
import works.hop.field.jdbc.repository.AccountRepo;

public class AccountExample {

    public static void main(String[] args) {
        AccountRepo accountRepo = new AccountRepo();
        Account account = accountRepo.findByUsername("bigben");
        System.out.println(account);

        Account account1 = accountRepo.findById(account.id);
        System.out.println(account1);

//        Account newAccount = new Account();
//        newAccount.
    }
}
