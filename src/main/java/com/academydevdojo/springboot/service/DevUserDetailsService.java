package com.academydevdojo.springboot.service;

import com.academydevdojo.springboot.repository.DevUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DevUserDetailsService implements UserDetailsService {

    private final DevUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        return Optional.ofNullable(repository.findByUsername(userName))
            .orElseThrow(() -> new UsernameNotFoundException("User Name not found"));
    }
}
