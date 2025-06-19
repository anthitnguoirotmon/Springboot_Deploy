package com.theanh1301.myapp.controller;


import com.theanh1301.myapp.dto.request.NormalizeApiRequest;
import com.theanh1301.myapp.dto.request.UserCreationRequest;
import com.theanh1301.myapp.dto.request.UserUpdateRequest;
import com.theanh1301.myapp.dto.response.UserResponse;
import com.theanh1301.myapp.mapper.UserMapper;
import com.theanh1301.myapp.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class ApiUserController {


    UserService userService;


    UserMapper userMapper;




    //@Valid de validation theo rule ben dto.request
    @PostMapping
    public NormalizeApiRequest<UserResponse> createUser(@RequestBody
                               @Valid  UserCreationRequest request)
    {
        NormalizeApiRequest<UserResponse> apiResponse = new NormalizeApiRequest<>();
         apiResponse.setResult(userService.createUser(request));
         return apiResponse;
    }


//    @PreAuthorize("hasRole('ADMIN')")
    //Có thể kiểm tra quyền permission luôn
//    @PreAuthorize("hasRole('ADMIN')") // do mình có thêm tiền tố ROLE_
    @PreAuthorize("hasAuthority('UPDATE_DATA')") // nên permission mình dùng asAuthority() để map chính xác
    @GetMapping
    public List<UserResponse> getUser(){
        log.info("Preauthorize chặn từ  đầu nếu như không có quyền luôn nên sẽ không thấy dòng này");
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username : {}", authentication.getName());
        authentication.getAuthorities().forEach(authority -> log.info(authority.getAuthority()));

        return userService.getAllUser();
    }

//    @PostAuthorize("hasRole('ADMIN')")
    //Chi lay duoc thong tin cua chinh minh
    //returnObject.username (username la attribute cua UserResponse)
    @PostAuthorize("returnObject.username == authentication.name")
    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable(value = "userId") String id){
        log.info("Postauthorize chạy hết hàm rồi mới ktra quyền nên log này vẫn được in ra ");
        return userService.getUserById(id);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@PathVariable(value="userId") String id, @RequestBody UserUpdateRequest request){
        return userService.updateUserById(id, request);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable(value = "userId")  String id){
        userService.deleteUserById(id);
        return "Xóa thành công user với id:" + id ;
    }



    //hoạc có thể thêm
    @GetMapping("/current_user")
    public NormalizeApiRequest<UserResponse> getCurrentUser(){
        return NormalizeApiRequest.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

//    @GetMapping("/current_user")
//    public UserResponse getCurrentUser(){
//        return userService.getMyInfo();
//    }
}
