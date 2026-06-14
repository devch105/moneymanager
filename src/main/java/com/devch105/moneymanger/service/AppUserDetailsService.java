package com.devch105.moneymanger.service;

import com.devch105.moneymanger.entity.ProfileEntity;
import com.devch105.moneymanger.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService  implements UserDetailsService {
    private final ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
       ProfileEntity existingprofile =  profileRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(email));

       return User.builder()
               .username(existingprofile.getEmail())
               .password(existingprofile.getPassword())
               .authorities(Collections.emptyList())
               .build();
    }
}
