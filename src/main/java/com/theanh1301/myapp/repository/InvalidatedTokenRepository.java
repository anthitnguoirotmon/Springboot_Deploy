package com.theanh1301.myapp.repository;

import com.theanh1301.myapp.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken,String> {
}
