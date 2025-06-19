package com.theanh1301.myapp.controller;


import com.theanh1301.myapp.dto.request.NormalizeApiRequest;
import com.theanh1301.myapp.dto.request.PermissionRequest;
import com.theanh1301.myapp.dto.request.RoleRequest;
import com.theanh1301.myapp.dto.response.PermissionResponse;
import com.theanh1301.myapp.dto.response.RoleResponse;
import com.theanh1301.myapp.mapper.RoleMapper;
import com.theanh1301.myapp.service.RoleService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class ApiRoleController {

    RoleService roleService;
    RoleMapper roleMapper;

    @GetMapping
    public NormalizeApiRequest<List<RoleResponse>> getAllRoles() {
        return NormalizeApiRequest.<List<RoleResponse>>builder().result(roleService.getAllRoles()).build();
    }


    @PostMapping
    public NormalizeApiRequest<RoleResponse> addRole(@RequestBody RoleRequest request){
        return NormalizeApiRequest.<RoleResponse>builder().result(roleService.createRole(request)).build() ;
    }


    @DeleteMapping("/{roleId}")
    public String deleteRole(@PathVariable String roleId){
        roleService.deleteRole(roleId);
        return " Xóa thành công permission:" +roleId;
    }

}
