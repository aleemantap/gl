package com.project.gl.service;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.KategoriRepository;
import com.project.gl.service.GlAccountTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.project.gl.helper.Util;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GlAccountTypeServiceImpl implements GlAccountTypeService {

    private final KategoriRepository repository;
    private final Util util;

    @Override
    public List<GlAccountType> getAll() {
        return repository.findAll();
    }

    @Override
    public GlAccountType getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori not found with id: " + id));
    }

    @Override
    public GlAccountType create(GlAccountType request) {
        return repository.save(request);
    }

    @Override
    public GlAccountType update(Integer id, GlAccountType request) {
        GlAccountType existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori not found with id: " + id));
        
        existing.setName(request.getName());
        existing.setNote(request.getNote());
        
        return repository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Kategori not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public PageResponse<GlAccountType> getAllWithFilter(
            int page,
            int size,
            String sortBy,
            String orderDir,
            String name
          
    ) {
        Sort.Direction direction = Sort.Direction.fromString(orderDir);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        
        Page<GlAccountType> resultPage;
        
        boolean hasName = StringUtils.hasText(name);
        // boolean hasNote = StringUtils.hasText(note);
        
       // if (hasName && hasNote) {
        //    resultPage = repository.findByNameContainingIgnoreCaseAndNoteContainingIgnoreCase(name, note, pageable);
        //} else 
        if (hasName) {
            resultPage = repository.findByNameContainingIgnoreCase(name, pageable);
        } 
        //else if (hasNote) {
            // resultPage = repository.findByNoteContainingIgnoreCase(note, pageable);
        //} 
        else {
            resultPage = repository.findAll(pageable);
        }
        
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
    public Optional<GlAccountType> getKategoriById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public GlAccountType saveKategori(GlAccountType kategori) {
        return repository.save(kategori);
    }
    

    @Override
    public GlAccountType updateKategori(GlAccountType kategori) {
        return repository.save(kategori);
    }
    
    @Override
    public void deleteKategori(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<GlAccountType> getAllWithFilterExport(String idsJson, String kategori) {

        List<Long> ids = util.parseIdsJson(idsJson);

        boolean idsEmpty = (ids == null || ids.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : ids; 

        List<GlAccountType> rows =  repository.findByAllExport(
                safeIds, 
                idsEmpty, 
                kategori
        );

        return rows;

       
    }
}