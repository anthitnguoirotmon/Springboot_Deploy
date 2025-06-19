package com.theanh1301.myapp.repository;

import com.theanh1301.myapp.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PermissionRepository extends JpaRepository<Permission,String> {


}
