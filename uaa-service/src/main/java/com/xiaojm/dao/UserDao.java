package com.xiaojm.dao;


import com.xiaojm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by xiaojm
 */

public interface UserDao extends JpaRepository<User, Long> {

	User findByUsername(String username);
}
