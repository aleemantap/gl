package com.project.gl.repo;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.User;
import com.project.gl.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
       "LEFT JOIN FETCH u.userGroup " +
       "LEFT JOIN FETCH u.cabang " +
       "WHERE (:cabang IS NULL OR u.cabang.id = :cabang) " +
       "AND (:userGroupId IS NULL OR u.userGroup.id = :userGroupId) " +
       "AND (:name IS NULL OR LOWER(TRIM(u.name)) LIKE LOWER(CONCAT('%', TRIM(:name), '%'))) " +
       "AND (:status IS NULL OR :status = '' OR u.active = :status)")
    Page<User> findAllWithFilters(@Param("name") String name, 
                              @Param("userGroupId") Integer userGroupId,                           
                              @Param("status") String status,
                              @Param("cabang") Integer cabang,
                              Pageable pageable);

     @Query("SELECT u FROM User u " +
       "LEFT JOIN FETCH u.userGroup " +
       "LEFT JOIN FETCH u.cabang " +
       "WHERE (:idsEmpty = true OR u.id IN (:ids)) " +
       "AND (:cabang IS NULL OR u.cabang.id = :cabang) " +
       "AND (:userGroupId IS NULL OR u.userGroup.id = :userGroupId) " +
       "AND (:name IS NULL OR LOWER(TRIM(u.name)) LIKE LOWER(CONCAT('%', TRIM(:name), '%'))) " +
       "AND (:status IS NULL OR :status = '' OR u.active = :status)")
    List<User> findAllWithFiltersExport(
                                 @Param("ids") List<Long> ids,
                                 @Param("idsEmpty") boolean idsEmpty,
                                 @Param("name") String name, 
                                 @Param("userGroupId") Integer userGroupId,                           
                                 @Param("status") String status,
                                 @Param("cabang") Integer cabang
                              );

      

   
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);    
    List<User> findByActive(String active);
    @Query("SELECT u FROM User u WHERE u.active = 'Y' ORDER BY u.name ASC")
    List<User> findAllActiveUsers();    
    // Mendapatkan user non-aktif
    @Query("SELECT u FROM User u WHERE u.active = 'N' ORDER BY u.name ASC")
    List<User> findAllInactiveUsers();
    
    // Search user
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<User> searchUsers(@Param("search") String search);
    
    // Count active users
    long countByActive(String active);

    @Query("SELECT u FROM User u " +
       "LEFT JOIN FETCH u.userGroup " +
       "LEFT JOIN FETCH u.cabang " +
       "WHERE u.email = :email")
    Optional<User> findByEmailWithRelations(@Param("email") String email);
}