package com.project.gl.repo;

import com.project.gl.model.entity.Component;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends JpaRepository<Component, Long> {

    Optional<Component> findByComponentCodeAndIsActiveTrue(String componentCode);

    @Query("SELECT c FROM Component c WHERE " +
           "(:code IS NULL OR :code = '' OR c.componentCode LIKE %:code%) AND " +
           "(:name IS NULL OR :name = '' OR c.componentName LIKE %:name%)")
    Page<Component> findByComponentCodeAndComponentName(
            @Param("code") String code,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("SELECT c FROM Component c WHERE " +
           "(:idsEmpty = true OR c.id IN (:ids)) AND"+
           "(:kode IS NULL OR :kode = '' OR c.componentCode LIKE %:kode%) AND " +
           "(:nama IS NULL OR :nama = '' OR c.componentName LIKE %:nama%)")
    List<Component> findAllExport(
            @Param("ids") List<Long> ids,
            @Param("idsEmpty") boolean idsEmpty,
            @Param("nama") String nama,
            @Param("kode") String kode
    );



    
    
    boolean existsByComponentCode(String componentCode);    
    boolean existsByComponentCodeAndIdNot(String componentCode, Long id);
}