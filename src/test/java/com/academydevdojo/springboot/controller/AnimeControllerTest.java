package com.academydevdojo.springboot.controller;

import static com.academydevdojo.springboot.util.AnimeBuilder.createAnimeToBeSaved;
import static com.academydevdojo.springboot.util.AnimeBuilder.createValidAnime;
import static com.academydevdojo.springboot.util.AnimeBuilder.createValidUpdateAnime;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.academydevdojo.springboot.domain.Anime;
import com.academydevdojo.springboot.service.AnimeService;
import java.util.Collections;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController controller;

    @Mock
    private AnimeService service;

    @BeforeEach
    void setUp() {

        PageImpl<Anime> animePage = new PageImpl<>(List.of(createValidAnime()));
        when(service.listAll(ArgumentMatchers.any())).thenReturn(animePage);

        when(service.findById(ArgumentMatchers.anyInt()))
            .thenReturn(createValidAnime());

        when(service.findByName(ArgumentMatchers.anyString()))
            .thenReturn(List.of(createValidAnime()));

        when(service.save(ArgumentMatchers.any(Anime.class)))
            .thenReturn(createValidAnime());

        when(service.save(createValidAnime())).thenReturn(createValidUpdateAnime());

        doNothing().when(service).delete(ArgumentMatchers.anyInt());

    }

    @Test
    @DisplayName("Should Return Pageable List Anime")
    void list_return_list_of_animes_inside_page_object_when_successful() {

        String expectedName = createValidAnime().getName();
        Page<Anime> animePage = controller.list(null).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("Should Return Anime By Id")
    void findById_return_anime_by_id() {

        Integer expectedId = createValidAnime().getId();
        Anime anime = controller.findById(1).getBody();

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Should Return Anime By Name")
    void findByName_return_anime_by_name() {

        String expectedName = createValidAnime().getName();
        List<Anime> animes = controller.findByName("").getBody();

        Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isNotNull().isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Should Return Empty List When Anime Not Found")
    void findByName_return_empty_list_when_anime_not_found() {

        when(service.findByName(ArgumentMatchers.anyString()))
            .thenReturn(Collections.emptyList());

        List<Anime> animes = controller.findByName("").getBody();

        Assertions.assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should Save Anime When Successful")
    void save_anime_when_successful() {

        Integer expectedId = createValidAnime().getId();

        Anime animeToBeSaved = createAnimeToBeSaved();

        Anime anime = controller.save(animeToBeSaved).getBody();

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull();
        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    /**
     * Esse teste do método delete no Controller é passível executa-lo de duas formas:
     * 1 - Utilizar o AssertThatCode e validar com o doesNotThrowAnyException()
     * 2 - Utilizar o Response entity e validar o Status Code de retorno que nesse
     * caso é o 204 NO CONTENT
     */
    @Test
    @DisplayName("Should Delete Anime When Successful")
    void delete_anime_when_successful() {

        Assertions.assertThatCode(() -> controller.delete(1)).doesNotThrowAnyException();

        ResponseEntity<Void> entity = controller.delete(1);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    /**
     * Esse teste do método replace no Controller é passível executa-lo de duas formas:
     * 1 - Utilizar o AssertThatCode e validar com o doesNotThrowAnyException()
     * 2 - Utilizar o Response entity e validar o Status Code de retorno que nesse
     * caso é o 204 NO CONTENT
     */

    @Test
    @DisplayName("Should Update Anime When Successful")
    void update_anime_when_successful() {

        ResponseEntity<Void> responseEntity = controller.update(createValidAnime());

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(responseEntity.getBody()).isNull();
    }
}