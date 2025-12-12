package org.example.o_lim.repository;

import jakarta.validation.constraints.NotNull;
import org.example.o_lim.common.enums.RoleType;
import org.example.o_lim.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, RoleType> {

    Optional<Role> findByName(@NotNull(message = "role은 필수입니다.") RoleType role);
}
