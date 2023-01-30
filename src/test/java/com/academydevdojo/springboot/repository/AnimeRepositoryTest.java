package com.academydevdojo.springboot.repository;

import static com.academydevdojo.springboot.util.AnimeBuilder.createAnimeToBeSaved;

import com.academydevdojo.springboot.domain.Anime;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AnimeRepositoryTest {
    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Should Save Anime When Successful")
    void should_save_persist_anime_when_successful() {

        Anime animeToBeSaved = createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        Assertions.assertThat(animeSaved).isNotNull();
        Assertions.assertThat(animeSaved.getId()).isNotNull();
        Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
    }

    @Test
    @DisplayName("Should Update and Save Anime When Successful")
    void should_update_and_persist_anime_when_successful() {

        Anime animeToBeSaved = createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        animeSaved.setName("Cavaleiros dos Zodíacos");

        Anime animeUpdated = this.animeRepository.save(animeSaved);

        Assertions.assertThat(animeUpdated).isNotNull();
        Assertions.assertThat(animeUpdated.getId()).isNotNull();
        Assertions.assertThat(animeUpdated.getName()).isEqualTo(animeSaved.getName());
    }

    @Test
    @DisplayName("Should Delete Anime When Successful")
    void should_delete__anime_when_successful() {

        Anime animeToBeSaved = createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

       this.animeRepository.delete(animeToBeSaved);

        Optional<Anime> animeOptional = this.animeRepository.findById(animeSaved.getId());

        Assertions.assertThat(animeOptional).isEmpty();
    }

    @Test
    @DisplayName("Should Find Anime By Name When Successful")
    void should_find_anime_by_name_when_successful() {

        Anime animeToBeSaved = createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        String name = animeSaved.getName();

        List<Anime> animes = this.animeRepository.findByName(name);

        Assertions.assertThat(animes).isNotEmpty();
        Assertions.assertThat(animes).contains(animeSaved);
    }

    @Test
    @DisplayName("Should Returns Empty List When Name Not Found")
    void should_return_empty_list_when_anime_not_found() {

        List<Anime> animes = this.animeRepository.findByName("John Doe");

        Assertions.assertThat(animes).isEmpty();
    }

    @Test
    @DisplayName("Should Throws ConstraintViolationException When name is empty")
    void should_throw_constraint_violation_exception_when_name_is_empty() {

        Anime anime = new Anime();

        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
            .isThrownBy(() -> animeRepository.save(anime))
            .withMessageContaining("The name of this anime cannot be empty");

    }
}