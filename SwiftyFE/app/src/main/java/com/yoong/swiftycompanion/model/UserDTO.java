package com.yoong.swiftycompanion.model;

public record UserDTO(
        Integer id,
        String email,
        String login,
        String first_name,
        String last_name,
        Image image,
        CursusUser[] cursus_users,
        ProjectUser[] projects_users
){}

record Versions(
        String large,
        String medium,
        String small,
        String micro
) {}