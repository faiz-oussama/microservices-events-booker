package com.ticketing.authservice.repository;

import com.ticketing.authservice.model.Role;
import com.ticketing.authservice.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleType(RoleType roleType);
}
