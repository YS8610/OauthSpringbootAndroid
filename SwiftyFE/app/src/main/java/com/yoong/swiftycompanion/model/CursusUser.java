package com.yoong.swiftycompanion.model;

public record CursusUser(
        Integer id,
        Cursus cursus,
        String grade,
        Double level,
        Skill[] skills,
        Integer wallet,
        Integer correction_point
) {
}
