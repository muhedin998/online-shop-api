package com.example.online_shop.user.repository;

import com.example.online_shop.user.model.Role;
import com.example.online_shop.user.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
