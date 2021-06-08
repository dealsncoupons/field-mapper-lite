package works.hop.sample1.hydrate.app;

import works.hop.hydrate.jdbc.context.DbSelector;
import works.hop.hydrate.jdbc.context.SelectTemplate;
import works.hop.sample1.hydrate.entity.Club;

public class ClubExample {

    public static void main(String[] args) {
        String query = "select c.* from tbl_club c where c.title = ?";
        Club club = SelectTemplate.selectOne(new Club(), query, DbSelector.selector(), new Object[]{"book club"});
        System.out.printf("Retrieved club with id %s%n", club.getId());
    }
}
