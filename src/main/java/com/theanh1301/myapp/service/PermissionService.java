package com.theanh1301.myapp.service;


import com.theanh1301.myapp.dto.request.PermissionRequest;
import com.theanh1301.myapp.dto.response.PermissionResponse;
import com.theanh1301.myapp.entity.Permission;
import com.theanh1301.myapp.mapper.PermissionMapper;
import com.theanh1301.myapp.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j   // tạo ra biến log
@Service
@RequiredArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE , makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;



    public PermissionResponse createPermission(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);// nó tự set các key vào luôn
        permissionRepository.save(permission); // thực hiện save xuống csdl
        return permissionMapper.toPermissionResponse(permission); // nhma mình trả ra là dto
    }

    public List<PermissionResponse> getAllPermission(){
        return permissionRepository.findAll().stream().map(permissionMapper::toPermissionResponse).collect(Collectors.toList());
        //mình vẫn trả ra dto
    }

    public void deletePermission(String id){permissionRepository.deleteById(id);}
}
