package com.academydevdojo.springboot.util;

import com.academydevdojo.springboot.domain.Anime;

public class AnimeBuilder {

    public static Anime createAnimeToBeSaved() {

        return Anime.builder()
            .name("DBZ")
            .build();
    }

    public static Anime createValidAnime() {

        return Anime.builder()
            .id(1)
            .name("DBZ")
            .build();
    }

    public static Anime createValidUpdateAnime() {

        return Anime.builder()
            .name("Tensei Shitara Slime Datta Ken 2")
            .id(1)
            .build();
    }
}
