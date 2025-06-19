package com.theanh1301.myapp.mapper;


import com.theanh1301.myapp.dto.request.RoleRequest;
import com.theanh1301.myapp.dto.response.RoleResponse;
import com.theanh1301.myapp.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    //bỏ qua perrmision không map phải tu map
    @Mapping(target = "permissions", ignore = true) // bo qua
    Role toRole(RoleRequest roleRequest);
    RoleResponse toRoleResponse(Role role);
}
