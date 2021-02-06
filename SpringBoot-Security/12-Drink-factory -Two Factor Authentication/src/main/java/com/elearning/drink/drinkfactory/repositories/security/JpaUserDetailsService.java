package com.elearning.drink.drinkfactory.repositories.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //transactional context/scope was limited to findByUsername call and
        //since getAuthorities is lazy loaded, we ran into transactional problem
        //when we try to load authorities outside the transaction scope-> Hence the need to use '@Transactional'
        //Another solution would be, to eagerly load authorities and to avoid a round trip to the db!
      return  userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username " + username + "Not found"));

       /* return new org.springframework.security.core.userdetails
                .User(
                        domainUser.getUsername(),
                        domainUser.getPassword(),
                        domainUser.getEnabled(),
                        domainUser.getAccountNonExpired(),
                        domainUser.getCredentialsNonExpired(),
                        domainUser.getAccountNonLocked(),
                        getConvertedAuthorities(domainUser.getAuthorities()));*/
    }

   /* private Set<GrantedAuthority> getConvertedAuthorities(Set<Authority> authorities) {
        return (authorities == null || authorities.size() == 0) ? new HashSet<>() :
                authorities.stream()
                        .map(Authority::getPermission)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
    }*/
}
