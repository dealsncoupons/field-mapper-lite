package works.hop.javro.jdbc.example;

import works.hop.javro.jdbc.entity.Club;
import works.hop.javro.jdbc.repository.ClubRepo;
import works.hop.javro.jdbc.template.CrudRepoFactory;

import java.util.Optional;

public class ClubExample {

    public static void main(String[] args) {
        ClubRepo clubRepo = CrudRepoFactory.getInstance(ClubRepo.class);
        Optional<Club> findClub = clubRepo.findByUnique("title", "book club");
        findClub.ifPresent(club -> {
            System.out.println(club);
            Optional<Club> club1 = clubRepo.findById(club.id);
            club1.ifPresent(System.out::println);
        });
    }
}
