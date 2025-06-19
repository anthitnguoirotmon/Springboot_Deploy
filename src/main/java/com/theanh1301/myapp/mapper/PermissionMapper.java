package com.theanh1301.myapp.mapper;


import com.theanh1301.myapp.dto.request.PermissionRequest;
import com.theanh1301.myapp.dto.response.PermissionResponse;
import com.theanh1301.myapp.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toPermissionResponse(Permission permission);
    Permission toPermission(PermissionRequest request);
}
