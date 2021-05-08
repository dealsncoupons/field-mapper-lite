package works.hop.field.jdbc.example;

import works.hop.field.jdbc.entity.Club;
import works.hop.field.jdbc.repository.ClubRepo;

public class ClubExample {

    public static void main(String[] args) {
        ClubRepo clubRepo = new ClubRepo();
        Club club = clubRepo.findByTitle("book club");
        System.out.println(club);

        Club club1 = clubRepo.findById(club.id);
        System.out.println(club1);
    }
}
