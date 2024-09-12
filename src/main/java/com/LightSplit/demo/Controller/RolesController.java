package com.LightSplit.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.LightSplit.demo.Model.Roles;
import com.LightSplit.demo.Repository.RoleRepository;

@RestController
public class RolesController {
    
    @Autowired 
    private RoleRepository roleRepo;

    // 1. save role to roleRepo; findByName
    @PostMapping("/role") 
    public ResponseEntity<?> saveRole(@RequestBody Roles role) {
        if(roleRepo.existsByName(role.getName())) {
            return new ResponseEntity<>("Role already existed", HttpStatus.OK);
        } 
        roleRepo.save(role);
        return new ResponseEntity<>(role, HttpStatus.OK);
    } 
}
