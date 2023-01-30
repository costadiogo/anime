package com.academydevdojo.springboot.controller;


import com.academydevdojo.springboot.domain.Anime;
import com.academydevdojo.springboot.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/animes")
@RequiredArgsConstructor
public class AnimeController {
    private final AnimeService animeService;

    @PostMapping("/create")
    @Operation(summary = "Create a new Anime", tags = "Create")
    public ResponseEntity<Anime> save(@RequestBody @Valid Anime anime) {

        return ResponseEntity.ok(animeService.save(anime));
    }

    @GetMapping
    @Operation(summary = "List All Animes Paginated", tags = "Read")
    public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable) {

        return ResponseEntity.ok(animeService.listAll(pageable));
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "List an Anime by Id", tags = "Read")
    public ResponseEntity<Anime> findById(@PathVariable int id) {
        return ResponseEntity.ok(animeService.findById(id));
    }

    @GetMapping(path = "/auth/find-by/{id}")
    @Operation(summary = "List an Anime by Id When User is Authenticated", tags = "Read")
    public ResponseEntity<Anime> findByIdAuthenticationPrincipal(@PathVariable int id, @AuthenticationPrincipal
        UserDetails userDetails) {
        return ResponseEntity.ok(animeService.findById(id));
    }

    @GetMapping(path = "/find-by/name")
    @Operation(summary = "List an Anime by Name", tags = "Read")
    public ResponseEntity<List<Anime>> findByName(@RequestParam(value = "name") String anime) {
        return ResponseEntity.ok(animeService.findByName(anime));
    }

    @PutMapping
    @Operation(summary = "Update an Anime", tags = "Update")
    public ResponseEntity<Void> update(@RequestBody Anime anime) {
        animeService.update(anime);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/admin/{id}")
    @Operation(summary = "Delete an Anime", tags = "Delete")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successful Operation"),
        @ApiResponse(responseCode = "400", description = "When Anime Does Not Exist in The Database")
    })
    public ResponseEntity<Void> delete(@PathVariable  int id) {
        animeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
