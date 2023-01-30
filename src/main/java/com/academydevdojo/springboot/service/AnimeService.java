package com.academydevdojo.springboot.service;

import com.academydevdojo.springboot.domain.Anime;
import com.academydevdojo.springboot.exception.BadRequestException;
import com.academydevdojo.springboot.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {

    public final AnimeRepository animeRepository;
    public Page<Anime> listAll(Pageable pageable) {

        return animeRepository.findAll(pageable);
    }

    public List<Anime> findByName(String name) {
        return animeRepository.findByName(name);
    }

    public Anime findById(int id) {
        return animeRepository.findById(id).orElseThrow(() -> new BadRequestException("Anime not found"));
    }

    @Transactional
    public Anime save(Anime anime) {

        System.out.println("Anime 1 :" + anime);

        return animeRepository.save(anime);
    }

    public void delete(int id) {
        animeRepository.delete(findById(id));
    }

    public void update(Anime anime) {

        animeRepository.save(anime);
    }
}
