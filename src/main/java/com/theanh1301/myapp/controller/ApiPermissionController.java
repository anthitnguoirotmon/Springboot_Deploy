package com.theanh1301.myapp.controller;


import com.theanh1301.myapp.dto.request.NormalizeApiRequest;
import com.theanh1301.myapp.dto.request.PermissionRequest;
import com.theanh1301.myapp.dto.response.PermissionResponse;
import com.theanh1301.myapp.mapper.PermissionMapper;
import com.theanh1301.myapp.service.PermissionService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class ApiPermissionController {

    PermissionService permissionService;
    PermissionMapper permissionMapper;



    @GetMapping
    public NormalizeApiRequest<List<PermissionResponse>> getAllPermissions() {
       return NormalizeApiRequest.<List<PermissionResponse>>builder().result(permissionService.getAllPermission()).build();
    }

    //nếu không dùng builder
    //return new NormalizeApiRequest<>(
    //        1000, // code mặc định của bạn
    //        null, // message (do builder không set message)
    //        permissionService.getAllPermission() // result
    //    );




    @PostMapping
    public NormalizeApiRequest<PermissionResponse> addPermission(@RequestBody PermissionRequest request){
        return NormalizeApiRequest.<PermissionResponse>builder().result(permissionService.createPermission(request)).build() ;
    }


    @DeleteMapping("/{permissionId}")
    public String deletePermission(@PathVariable String permissionId){
         permissionService.deletePermission(permissionId);
         return " Xóa thành công permission:" +permissionId;
    }

}
