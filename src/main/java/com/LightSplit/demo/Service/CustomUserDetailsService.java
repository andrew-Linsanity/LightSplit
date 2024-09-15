package com.LightSplit.demo.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.LightSplit.demo.Model.Roles;
import com.LightSplit.demo.Model.UserEntity;
import com.LightSplit.demo.Repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByUsername(username).orElseThrow(() -> new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'"));
        return new User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles())); 
    }
    
    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Roles> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
