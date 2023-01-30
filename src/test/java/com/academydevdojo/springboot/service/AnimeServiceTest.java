package com.academydevdojo.springboot.service;

import static com.academydevdojo.springboot.util.AnimeBuilder.createAnimeToBeSaved;
import static com.academydevdojo.springboot.util.AnimeBuilder.createValidAnime;
import static com.academydevdojo.springboot.util.AnimeBuilder.createValidUpdateAnime;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.academydevdojo.springboot.domain.Anime;
import com.academydevdojo.springboot.exception.BadRequestException;
import com.academydevdojo.springboot.repository.AnimeRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService service;

    @Mock
   private AnimeRepository repository;

    @BeforeEach
    void setUp() {

        PageImpl<Anime> animePage = new PageImpl<>(List.of(createValidAnime()));
        when(repository.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(animePage);

        when(repository.findById(ArgumentMatchers.anyInt()))
            .thenReturn(Optional.of(createValidAnime()));

        when(repository.findByName(ArgumentMatchers.anyString()))
            .thenReturn(List.of(createValidAnime()));

        when(repository.save(ArgumentMatchers.any(Anime.class)))
            .thenReturn(createValidAnime());

        doNothing().when(repository).delete(ArgumentMatchers.any(Anime.class));

        when(repository.save(createValidAnime())).thenReturn(createValidUpdateAnime());

    }

    @Test
    @DisplayName("Should Return Pageable List Anime")
    void listAll_return_list_of_animes_inside_page_object_when_successful() {

        String expectedName = createValidAnime().getName();
        Page<Anime> animePage = service.listAll(PageRequest.of(1, 2));

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("Should Return Anime By Id")
    void findByIdOrThrowBadRequestException_return_anime_by_id() {

        Integer expectedId = createValidAnime().getId();
        Anime anime = service.findById(1);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Should Return BadRequestException Anime By Id Is Not Found")
    void findByIdOrThrowBadRequestException_return_an_exception_when_id_not_found() {

        when(repository.findById(ArgumentMatchers.anyInt()))
            .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class)
            .isThrownBy(() -> service.findById(1));
    }

    @Test
    @DisplayName("Should Return Anime By Name")
    void findByName_return_anime_by_name() {

        String expectedName = createValidAnime().getName();
        List<Anime> animes = service.findByName("");

        Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isNotNull().isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Should Return Empty List When Anime Not Found")
    void findByName_return_empty_list_when_anime_not_found() {

        when(service.findByName(ArgumentMatchers.anyString()))
            .thenReturn(Collections.emptyList());

        List<Anime> animes = service.findByName("");

        Assertions.assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should Save Anime When Successful")
    void save_anime_when_successful() {

        Integer expectedId = createValidAnime().getId();

        Anime animeToBeSaved = createAnimeToBeSaved();

        Anime anime = service.save(animeToBeSaved);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Should Delete Anime When Successful")
    void delete_anime_when_successful() {

        Assertions.assertThatCode(() -> service.delete(1)).doesNotThrowAnyException();

    }

    @Test
    @DisplayName("Should Update Anime When Successful")
    void update_anime_when_successful() {

        Anime validUpdatedAnime = createValidUpdateAnime();

        String expectedName = validUpdatedAnime.getName();

        Anime anime = service.save(createValidAnime());

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull();
        Assertions.assertThat(anime.getName()).isEqualTo(expectedName);

    }

}