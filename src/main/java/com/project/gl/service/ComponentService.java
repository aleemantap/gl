package com.project.gl.service;

import com.project.gl.model.request.ComponentRequest;
import com.project.gl.model.response.PageResponse;
import java.util.List;
import com.project.gl.model.entity.Component;

public interface ComponentService {
    PageResponse<Component> getAllWithFilter(int page, int size, String sortBy, 
                                            String orderDir, String code, String name);
    Component getById(Long id);
    Component create(ComponentRequest request, String username);
    Component update(Long id, ComponentRequest request, String username);
    
    List<Component> getAllWithFilterExport(
            String listIds,
            String nama,
            String kode
    );
    // void delete(Long id);
}