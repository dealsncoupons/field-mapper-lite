package works.hop.javro.jdbc.sample;

import works.hop.javro.jdbc.entity.Account;
import works.hop.javro.jdbc.entity.Member;
import works.hop.javro.jdbc.sample.account.IAccount;
import works.hop.javro.jdbc.sample.account.IMember;
import works.hop.javro.jdbc.sample.todo.ITodo;
import works.hop.javro.jdbc.sample.todo.Todo;

public class EntityInstance {

    public static <T extends Unreflect> T create(Class<?> type) {
        if (ITodo.class.isAssignableFrom(type)) {
            return (T) new Todo();
        }
        if (IAccount.class.isAssignableFrom(type)) {
            return (T) new Account();
        }
        if (IMember.class.isAssignableFrom(type)) {
            return (T) new Member();
        }
        throw new RuntimeException("Unexpected class - " + type.getName());
    }
}
