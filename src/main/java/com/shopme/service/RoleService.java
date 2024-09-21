package com.shopme.service;

import com.shopme.entity.Role;
import com.shopme.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public List<Role> getRoles(){
        return roleRepository.findAll();
    }

    public Set<Role> getAllRoleById(List<String> id){
        return new HashSet<>(roleRepository.findAllById(id));
    }
}
