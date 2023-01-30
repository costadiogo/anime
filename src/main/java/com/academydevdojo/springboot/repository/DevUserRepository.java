package com.academydevdojo.springboot.repository;

import com.academydevdojo.springboot.domain.Anime;
import com.academydevdojo.springboot.domain.DevUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DevUserRepository extends JpaRepository<DevUser, Integer> {

    DevUser findByUsername(String userName);
}
