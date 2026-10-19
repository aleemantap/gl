package com.project.gl.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.gl.model.entity.JournalLineTemplate;
import com.project.gl.model.response.JournalLineTemplateResponse;

public interface JournalLineTemplateRepository extends JpaRepository<JournalLineTemplate, Long> {


    @Query("SELECT COUNT(t) FROM JournalLineTemplate t WHERE t.journalId = :journalId")
    Long countByJournalId(@Param("journalId") Long journalId);

    @Query("""
        SELECT NEW com.project.gl.model.response.JournalLineTemplateResponse(
            jlt.id,
            jlt.journalId,
            jlt.glAccountId,
            gl.nomorGl,
            gl.namaGlAccount,
            gl.glAccountType.name, 
            jlt.dk,
            jlt.lineOrder,
            jlt.description,
            c.componentName,
            c.componentCode,
            p.componentId,
            jlt.isActive,
            p.productRuleId,
            jlt.cashMovementType

        )
        FROM JournalLineTemplate jlt
        JOIN NomorGlAccount gl ON gl.id = jlt.glAccountId
        JOIN ProductRuleComponent p ON p.id = jlt.productRuleComponentId
        JOIN Component c ON c.id = p.componentId
        WHERE jlt.journalId = :journalId
        ORDER BY jlt.lineOrder ASC
    """)
    List<JournalLineTemplateResponse> findDetailByJournalId(@Param("journalId") Long journalId);
    List<JournalLineTemplate> findByJournalIdOrderByLineOrderAsc(Long journalId);
    // Long countByComponentId(@Param("componentId") Long componentId);
     // Or if ProductRuleComponent has a field named componentId
    Long countByProductRuleComponent_ComponentId(Long componentId);

    List<JournalLineTemplate> findByProductRuleComponentId(Long productRuleComponentId);

    //  List<JournalLineTemplate>
    // findByProductRuleComponent_Id(Long productRuleComponentId);

    @Modifying
    @Query("DELETE FROM JournalLineTemplate j WHERE j.journalId = :journalId")
    void deleteByJournalId(@Param("journalId") Long journalId);

}