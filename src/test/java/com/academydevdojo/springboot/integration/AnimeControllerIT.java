package com.academydevdojo.springboot.integration;

import static com.academydevdojo.springboot.util.AnimeBuilder.createAnimeToBeSaved;
import static com.academydevdojo.springboot.util.AnimeBuilder.createValidAnime;

import com.academydevdojo.springboot.domain.Anime;
import com.academydevdojo.springboot.domain.DevUser;
import com.academydevdojo.springboot.repository.AnimeRepository;
import com.academydevdojo.springboot.repository.DevUserRepository;
import com.academydevdojo.springboot.wrapper.PageableResponse;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private AnimeRepository repository;

    @Autowired
    private DevUserRepository userRepository;

    private static final  DevUser USER = DevUser.builder()
        .name("Jhon Doe")
        .password("{bcrypt}$2a$10$yWD5Y1.zr8TAmcGzqcTlBOjVQTRow1LNsSfy8Evoh63CAfF2z28Vm")
        .username("John Doe")
        .authorities("ROLE_USER")
        .build();

    private static final  DevUser ADMIN = DevUser.builder()
        .name("Diogo Costa")
        .password("{bcrypt}$2a$10$yWD5Y1.zr8TAmcGzqcTlBOjVQTRow1LNsSfy8Evoh63CAfF2z28Vm")
        .username("Diogo Costa")
        .authorities("ROLE_ADMIN, ROLE_USER")
        .build();

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + port)
                .basicAuthentication("Jhon Doe", "academy");

            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + port)
                .basicAuthentication("Diogo Costa", "academy");

            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("Should Return Pageable List Anime")
    void list_return_list_of_animes_inside_page_object_when_successful() {

        userRepository.save(USER);

        Anime savedAnime = repository.save(createAnimeToBeSaved());

        String expectedResponse = savedAnime.getName();

        PageableResponse<Anime> animePageableResponse =  testRestTemplateRoleUser.exchange("/v1/animes", HttpMethod.GET, null,
            new ParameterizedTypeReference<PageableResponse<Anime>>() {}).getBody();

        Assertions.assertThat(animePageableResponse).isNotNull();
        Assertions.assertThat(animePageableResponse.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(animePageableResponse.toList().get(0).getName()).isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("Should Return Anime By Id")
    void findById_return_anime_by_id() {

        userRepository.save(USER);

        Anime savedAnime = repository.save(createAnimeToBeSaved());
        Integer expectedId = savedAnime.getId();
        Anime anime = testRestTemplateRoleUser.getForObject("/v1/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Should Return Anime By Name")
    void findByName_return_anime_by_name() {

        userRepository.save(USER);

        Anime savedAnime = repository.save(createAnimeToBeSaved());
        String expectedResponse = savedAnime.getName();
        String url = String.format("/v1/animes/find-by?name=%s", expectedResponse);
        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should Return Empty List When Anime Not Found")
    void findByName_return_empty_list_when_anime_not_found() {

        userRepository.save(USER);

        List<Anime> animes = testRestTemplateRoleUser.exchange("/v1/animes/find-by?name=Jhon-Week", HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        Assertions.assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should Save Anime When Successful")
    void save_anime_when_successful() {

        userRepository.save(USER);

        Integer expectedId = createValidAnime().getId();

        Anime animeToBeSaved = createAnimeToBeSaved();

        Anime anime = testRestTemplateRoleUser.exchange("/v1/animes/create", HttpMethod.POST,
            createJsonHttpEntity(animeToBeSaved), Anime.class).getBody();

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull();
        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }
    @Test
    @DisplayName("Should Delete Anime When Successful")
    void delete_anime_when_successful() {

        userRepository.save(ADMIN);

        Anime savedAnime = repository.save(createAnimeToBeSaved());

        ResponseEntity<Void> anime = testRestTemplateRoleAdmin.exchange("/v1/animes/admin/{id}", HttpMethod.DELETE,
            null , Void.class, savedAnime.getId());

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should Return 403 when user is not ADMIN")
    void delete_return_403_when_role_is_not_admin_successful() {

        userRepository.save(USER);

        Anime savedAnime = repository.save(createAnimeToBeSaved());

        ResponseEntity<Void> anime = testRestTemplateRoleUser.exchange("/v1/animes/admin/{id}", HttpMethod.DELETE,
            null , Void.class, savedAnime.getId());

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should Update Anime When Successful")
    void replace_anime_when_successful() {

        userRepository.save(USER);

        Anime savedAnime = repository.save(createAnimeToBeSaved());

        savedAnime.setName("Naruto");

        ResponseEntity<Void> anime = testRestTemplateRoleUser.exchange("/v1/animes/", HttpMethod.PUT,
            new HttpEntity<>(savedAnime), Void.class);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private HttpEntity<Anime> createJsonHttpEntity(Anime anime) {
        return new HttpEntity<>(anime, createJsonHeader());
    }

    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

}
