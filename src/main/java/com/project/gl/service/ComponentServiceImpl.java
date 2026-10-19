package com.project.gl.service;

import com.project.gl.model.request.ComponentRequest;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Component;
import com.project.gl.model.entity.ProductRuleComponent;
import com.project.gl.repo.ComponentRepository;
import com.project.gl.repo.ProductRuleComponentRepository;
import com.project.gl.repo.JournalLineTemplateRepository;
import com.project.gl.service.ComponentService;
import lombok.RequiredArgsConstructor;
import com.project.gl.helper.Util;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ComponentServiceImpl implements ComponentService {
    
    private final ComponentRepository componentRepository;

    private final ProductRuleComponentRepository productRuleComponentRepository;

    private final JournalLineTemplateRepository journalLineTemplateRepository;

    private final Util util;
    
    @Override
    public PageResponse<Component> getAllWithFilter(int page, int size, String sortBy, 
                                                   String orderDir, String code, String name) {
        Sort.Direction direction = Sort.Direction.fromString(orderDir);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        
        Page<Component> resultPage = componentRepository.findByComponentCodeAndComponentName(
                code, name, pageable
        );
        
        return new PageResponse<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }
    
      @Override
    public Component getById(Long id) {
        return componentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Component not found with id: " + id));
    }
    
    @Override
    @Transactional
    public Component create(ComponentRequest request, String username) {
        if (componentRepository.existsByComponentCode(request.getComponentCode())) {
            throw new RuntimeException("Component code already exists: " + request.getComponentCode());
        }
        
        Component component = new Component();
        component.setComponentCode(request.getComponentCode());
        component.setComponentName(request.getComponentName());
        component.setDescription(request.getDescription());
        component.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        component.setCreatedBy(username);
        
        return componentRepository.save(component);
    }
    
    @Override
    @Transactional
    public Component update(Long id, ComponentRequest request, String username) {
        Component existing = getById(id);
        
        if (componentRepository.existsByComponentCodeAndIdNot(request.getComponentCode(), id)) {
            throw new RuntimeException("Component code already exists: " + request.getComponentCode());
        }
        
        existing.setComponentCode(request.getComponentCode());
        existing.setComponentName(request.getComponentName());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setUpdatedBy(username);
        
        return componentRepository.save(existing);
    }

     @Override
    public List<Component> getAllWithFilterExport(String idsJson, String nama,String kode) {

        List<Long> ids = util.parseIdsJson(idsJson);

        boolean idsEmpty = (ids == null || ids.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : ids; 

        List<Component> rows =  componentRepository.findAllExport(
                safeIds, 
                idsEmpty, 
                nama,
                kode
        );
        return rows;       
    }

    
    // @Override
    // @Transactional
    // public void delete(Long id) {

    //     Long sdf = productRuleComponentRepository.countByComponentId(id);
    //     Long sdfs = journalLineTemplateRepository.countByComponentId(id);
       
    //     if(sdf>0)
    //     {
    //         throw new RuntimeException("Component code in use");

    //     }
    //     if(sdfs>0)
    //     {

    //         throw new RuntimeException("Component code in use");

    //     }
            

    //     Component component = getById(id);
    //     componentRepository.delete(component);
    // }
}