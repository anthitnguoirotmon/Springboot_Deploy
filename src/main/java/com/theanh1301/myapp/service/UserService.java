package com.theanh1301.myapp.service;


import com.theanh1301.myapp.dto.request.RoleRequest;
import com.theanh1301.myapp.dto.request.UserCreationRequest;
import com.theanh1301.myapp.dto.request.UserUpdateRequest;
import com.theanh1301.myapp.dto.response.RoleResponse;
import com.theanh1301.myapp.dto.response.UserResponse;
import com.theanh1301.myapp.entity.User;
import com.theanh1301.myapp.enums.Role;

import com.theanh1301.myapp.exception.AppException;
import com.theanh1301.myapp.exception.ErrorCode;
import com.theanh1301.myapp.mapper.UserMapper;
import com.theanh1301.myapp.repository.RoleRepository;
import com.theanh1301.myapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;



@Slf4j  // tạo ra biến log
@Service
@RequiredArgsConstructor // thay cho autowride(tạo contructor cho mọi attribute final)
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)// private final -> cho tất cả biến
public class UserService {

    //Tạo contructor 2 tham số cho UserService -> không cần @Autowired
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request) {


        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTS);//quản lý bằng chính exception của mình
        }


        //Thay vì làm thủ công thì đã có mapstruct
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));



//        user.setUsername(request.getUsername());
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
//        user.setPassword(request.getPassword());
//        user.setBirthday(request.getBirthday());
//        user.setBirthday(request.getBirthday());
        HashSet<String>  role = new HashSet<>();
        role.add(Role.USER.name());
//        user.setRoles(role);


        userRepository.save(user);

        return userMapper.toUserResponse(user);

    }

    //Khi một user đăng nhập thì nó sẽ lưu thông tin trong SecurityContextHolder của SpringSecurity
    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        //Optional<User> nghĩa là giá trị trả về có thể là một User, hoặc không có gì (empty).
        User user  =  userRepository.findByUsername(username).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUser(){
        return
                userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList()); // userMapper::toUserResponse
        //tương đướng với u -> userMapper.toUserResponse(u)
        //findAll() của thằng JPA -> Select * FROM User
    }

    public UserResponse getUserById(String id){
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy User ")));
    }

    public UserResponse updateUserById(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));


        userMapper.updateUser(user,request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
//        user.setBirthday(request.getBirthday());
//        user.setPassword(request.getPassword());
        var roles = roleRepository.findAllById(request.getRoles()); // TÌM ROLE THEO TÊN TRONG ENUM
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    //này chưa bắt lỗi
    public void deleteUserById(String id){
        userRepository.deleteById(id);
    }
}
