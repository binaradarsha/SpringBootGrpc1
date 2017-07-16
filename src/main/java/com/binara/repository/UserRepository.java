package com.binara.repository;

import com.binara.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by binara on 7/16/17.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    User findOne(Long aLong);
}
