package com.LightSplit.demo.Controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LightSplit.demo.DTO.AuthResponseDTO;
import com.LightSplit.demo.DTO.LogInDTO;
import com.LightSplit.demo.DTO.RegisterDTO;
import com.LightSplit.demo.Model.Roles;
import com.LightSplit.demo.Model.UserEntity;
import com.LightSplit.demo.Repository.RoleRepository;
import com.LightSplit.demo.Repository.UserRepository;
import com.LightSplit.demo.Security.JWTGenerator;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // deleted filed: passwordEncoder

    @Autowired
    private JWTGenerator jwtGenerator;

    /* TODO: implement the logics to become an admin. */ 
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        if(userRepository.existsByUsername(registerDTO.getUsername())) {
            return new ResponseEntity<>("Username is taken! ", HttpStatus.BAD_REQUEST);
        } 
        
        UserEntity user = new UserEntity();
        user.setUsername(registerDTO.getUsername()); 

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword()); // encode the password 

        user.setPassword(encodedPassword);

        Roles roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User Registered.", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LogInDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), 
                    loginDTO.getPassword())); 

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
    } 
}