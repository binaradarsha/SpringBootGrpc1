package com.binara.repository;

import com.binara.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by binara on 7/16/17.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
}
