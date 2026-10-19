package com.project.gl.repo;


import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.response.UserResponse;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByName(String name);

   
    Optional<UserGroup> findOneById(Long id);

    @Query("SELECT u FROM UserGroup u " +
      
       "WHERE " +
       "(:name IS NULL OR LOWER(TRIM(u.name)) LIKE LOWER(CONCAT('%', TRIM(:name), '%'))) " +
       "")
    Page<UserGroup> findAllWithFilters(@Param("name") String name, 
                              Pageable pageable);



    // boolean existsByEmail(String email);
    // boolean existsByEmailAndIdNot(String email, Long id);    
    List<UserGroup> findByActive(String active);
    // @Query("SELECT u FROM User u WHERE u.active = 'Y' ORDER BY u.name ASC")
    // List<UserGroup> findAllActiveUsers();    
    // Mendapatkan user non-aktif
    @Query("SELECT u FROM User u WHERE u.active = 'N' ORDER BY u.name ASC")
    List<UserGroup> findAllInactiveUsers();
    
    // Search user
    // @Query("SELECT u FROM User u WHERE " +
    //        "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    //        "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    // List<User> searchUsers(@Param("search") String search);
    
    // Count active users
    long countByActive(String active);
}