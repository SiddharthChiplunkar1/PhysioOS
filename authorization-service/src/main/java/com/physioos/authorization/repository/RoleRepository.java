package com.physioos.authorization.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.physioos.authorization.entity.Role;
import com.physioos.authorization.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role,UUID> {
    List<Role> findByOrganizationId(UUID organizationId);
    Optional<Role> findByRoleName(RoleName roleName);

}
