package com.project.gl.repo;

import com.project.gl.model.entity.ApiClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ApiClientRepository extends JpaRepository<ApiClient, Integer> {

    Optional<ApiClient> findByClientId(String clientId);
    //Optional<ApiClientFinal> findByClientId(String clientId);
    boolean existsByClientId(String clientId);

    @Query("SELECT a FROM ApiClient a ORDER BY a.id ASC")
    Optional<ApiClient> findFirst();

    // Optional<ApiClientFinal> findByApiKeyAndActiveTrue(String apiKey);
    Optional<ApiClient> findById(Long id);

    @Query("SELECT a FROM ApiClient a " +
           "WHERE " +
           "(:clientName IS NULL OR LOWER(TRIM(a.clientName)) LIKE LOWER(CONCAT('%', TRIM(:clientName), '%'))) " +
           "AND (:clientId IS NULL OR LOWER(TRIM(a.clientId)) LIKE LOWER(CONCAT('%', TRIM(:clientId), '%'))) " +
           "AND (:active IS NULL OR a.active = :active) " +
           "")
    Page<ApiClient> findAllWithFilters(@Param("clientName") String clientName,
                                       @Param("clientId") String clientId,
                                       @Param("active") Boolean active,
                                       Pageable pageable);


}