package works.hop.javro.jdbc.repository;

import works.hop.javro.jdbc.entity.Account;
import works.hop.javro.jdbc.template.CrudRepo;

import java.util.UUID;

public interface AccountRepo extends CrudRepo<Account, UUID> {
}
