package com.yoong.swifty_companion.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDTO(
  Integer id,
  String email,
  String login,
  @JsonProperty("first_name") String firstName,
  @JsonProperty("last_name") String lastName,
  @JsonProperty("usual_full_name") String usualFullName,
  @JsonProperty("usual_first_name") String usualFirstName,
  String url,
  String phone,
  String displayname,
  String kind,
  Image image,
  @JsonProperty("staff?") Boolean staff,
  @JsonProperty("correction_point") Integer correctionPoint,
  @JsonProperty("pool_month") String poolMonth,
  @JsonProperty("pool_year") String poolYear,
  String location,
  Integer wallet,
  @JsonProperty("anonymize_date") String anonymizeDate,
  @JsonProperty("data_erasure_date") String dataErasureDate,
  @JsonProperty("created_at") String createdAt,
  @JsonProperty("updated_at") String updatedAt,
  @JsonProperty("alumnized_at") String alumnizedAt,
  @JsonProperty("alumni?") Boolean alumni,
  @JsonProperty("active?") Boolean active,
  List<Object> groups,
  @JsonProperty("cursus_users") List<CursusUser> cursusUsers,
  @JsonProperty("projects_users") List<ProjectUser> projectsUsers,
  @JsonProperty("languages_users") List<LanguageUser> languagesUsers,
  List<Achievement> achievements,
  List<Object> titles,
  @JsonProperty("titles_users") List<Object> titlesUsers,
  List<Object> partnerships,
  List<Object> patroned,
  List<Object> patroning,
  @JsonProperty("expertises_users") List<Object> expertisesUsers,
  List<Object> roles,
  List<Campus> campus,
  @JsonProperty("campus_users") List<CampusUser> campusUsers
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Image(
  String link,
  Versions versions
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Versions(
  String large,
  String medium,
  String small,
  String micro
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record CursusUser(
  Integer id,
  @JsonProperty("begin_at") String beginAt,
  @JsonProperty("end_at") String endAt,
  String grade,
  Double level,
  List<Skill> skills,
  @JsonProperty("cursus_id") Integer cursusId,
  @JsonProperty("has_coalition") Boolean hasCoalition,
  @JsonProperty("blackholed_at") String blackholedAt,
  @JsonProperty("created_at") String createdAt,
  @JsonProperty("updated_at") String updatedAt,
  UserRef user,
  Cursus cursus
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Skill(
  Integer id,
  String name,
  Double level
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record UserRef(
  Integer id,
  String email,
  String login,
  @JsonProperty("first_name") String firstName,
  @JsonProperty("last_name") String lastName,
  @JsonProperty("usual_full_name") String usualFullName,
  @JsonProperty("usual_first_name") String usualFirstName,
  String url,
  String phone,
  String displayname,
  String kind,
  Image image,
  @JsonProperty("staff?") Boolean staff,
  @JsonProperty("correction_point") Integer correctionPoint,
  @JsonProperty("pool_month") String poolMonth,
  @JsonProperty("pool_year") String poolYear,
  String location,
  Integer wallet,
  @JsonProperty("anonymize_date") String anonymizeDate,
  @JsonProperty("data_erasure_date") String dataErasureDate,
  @JsonProperty("created_at") String createdAt,
  @JsonProperty("updated_at") String updatedAt,
  @JsonProperty("alumnized_at") String alumnizedAt,
  @JsonProperty("alumni?") Boolean alumni,
  @JsonProperty("active?") Boolean active
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Cursus(
  Integer id,
  @JsonProperty("created_at") String createdAt,
  String name,
  String slug,
  String kind
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record ProjectUser(
  Integer id,
  Integer occurrence,
  @JsonProperty("final_mark") Integer finalMark,
  String status,
  @JsonProperty("validated?") Boolean validated,
  @JsonProperty("current_team_id") Integer currentTeamId,
  Project project,
  @JsonProperty("cursus_ids") List<Integer> cursusIds,
  @JsonProperty("marked_at") String markedAt,
  Boolean marked,
  @JsonProperty("retriable_at") String retriableAt,
  @JsonProperty("created_at") String createdAt,
  @JsonProperty("updated_at") String updatedAt
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Project(
  Integer id,
  String name,
  String slug,
  @JsonProperty("parent_id") Integer parentId
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record LanguageUser(
  Integer id,
  @JsonProperty("language_id") Integer languageId,
  @JsonProperty("user_id") Integer userId,
  Integer position,
  @JsonProperty("created_at") String createdAt
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Achievement(
  Integer id,
  String name,
  String description,
  String tier,
  String kind,
  Boolean visible,
  String image,
  @JsonProperty("nbr_of_success") Integer nbrOfSuccess,
  @JsonProperty("users_url") String usersUrl
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Campus(
  Integer id,
  String name,
  @JsonProperty("time_zone") String timeZone,
  CampusLanguage language,
  @JsonProperty("users_count") Integer usersCount,
  @JsonProperty("vogsphere_id") Integer vogsphereId,
  String country,
  String address,
  String zip,
  String city,
  String website,
  String facebook,
  String twitter,
  Boolean active,
  @JsonProperty("public") Boolean _public,
  @JsonProperty("email_extension") String emailExtension,
  @JsonProperty("default_hidden_phone") Boolean defaultHiddenPhone
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record CampusLanguage(
  Integer id,
  String name,
  String identifier,
  @JsonProperty("created_at") String createdAt,
  @JsonProperty("updated_at") String updatedAt
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record CampusUser(
  Integer id,
  @JsonProperty("user_id") Integer userId,
  @JsonProperty("campus_id") Integer campusId,
  @JsonProperty("is_primary") Boolean isPrimary,
  @JsonProperty("created_at") String createdAt,
  @JsonProperty("updated_at") String updatedAt
) {}
