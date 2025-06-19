package com.theanh1301.myapp.repository;

import com.theanh1301.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> { //Entity , (KieuDL khoa chinh)

    //extends JPA đỡ phải viết persist , merge , remove
    boolean existsByUsername(String username);//JPA tự viết
    Optional<User> findByUsername(String username); //neu null cung khong in ra exception
}
