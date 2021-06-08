// This entity builder class is AUTO-GENERATED, so there's no point in modifying it
package works.hop.sample1.hydrate.entity.builder;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import works.hop.sample1.hydrate.entity.Club;

public class ClubBuilder {
  private UUID id;

  private LocalDate dateCreated;

  private LocalDateTime lastUpdated;

  private String title;

  private String activity;

  private ClubBuilder() {
  }

  public static ClubBuilder newBuilder() {
    return new ClubBuilder();
  }

  public ClubBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public ClubBuilder dateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }

  public ClubBuilder lastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  public ClubBuilder title(String title) {
    this.title = title;
    return this;
  }

  public ClubBuilder activity(String activity) {
    this.activity = activity;
    return this;
  }

  public Club build() {
    return new Club(id,dateCreated,lastUpdated,title,activity);
  }
}
