package com.project.gl.helper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import com.project.gl.config.CustomUserPrincipal;
import com.project.gl.model.entity.NomorGlAccount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AutoCompleteHelper {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Object[]> getGELDebet(String keyword, String dc) {

        String sql = """
            SELECT
                c.id,
                c.nomor_gl,
                vc.name AS nameKategori,
                cb.nama_cabang AS namaCabang,
                c.nama_gl_account AS namaGL
            FROM nomor_gl_account c
            INNER JOIN kategori vc ON c.kategori_id = vc.id
            INNER JOIN cabang cb ON c.cabang_id = cb.id
            WHERE c.debet_kredit = :dc 
            AND (
                LOWER(c.nomor_gl) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(vc.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(cb.nama_cabang) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.nama_gl_account) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            ORDER BY c.nomor_gl
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dc", dc);
        query.setParameter("keyword", keyword.trim());
        
        return query.getResultList();
    }

    
    
}
