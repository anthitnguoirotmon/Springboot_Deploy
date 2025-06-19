package com.theanh1301.myapp.service;


import com.theanh1301.myapp.dto.request.RoleRequest;
import com.theanh1301.myapp.dto.response.PermissionResponse;
import com.theanh1301.myapp.dto.response.RoleResponse;
import com.theanh1301.myapp.entity.Role;
import com.theanh1301.myapp.mapper.RoleMapper;
import com.theanh1301.myapp.repository.PermissionRepository;
import com.theanh1301.myapp.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j // tạo ra biến log
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    public RoleResponse createRole(RoleRequest roleRequest) {
        Role role = roleMapper.toRole(roleRequest);
        var permissions = permissionRepository.findAllById(roleRequest.getPermissions()); // lấy theo ds role phải hiện có trong database
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }


    public List<RoleResponse> getAllRoles(){
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).collect(Collectors.toList());
        //mình vẫn trả ra dto
    }

    public void deleteRole(String id){roleRepository.deleteById(id);}



}
