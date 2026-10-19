package com.project.gl.service;


import com.project.gl.model.entity.CategoryTransaction;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.KategoriTransaksiRepository;
import com.project.gl.service.TransactionCategoriService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionCategoriServiceImpl implements TransactionCategoriService {

    private final KategoriTransaksiRepository repository;

    @Override
    public List<CategoryTransaction> getAll() {
        return repository.findAll();
    }

    @Override
    public CategoryTransaction getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori not found with id: " + id));
    }

    @Override
    public CategoryTransaction create(CategoryTransaction request) {
        return repository.save(request);
    }

    @Override
    public CategoryTransaction update(Integer id, CategoryTransaction request) {
        CategoryTransaction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori not found with id: " + id));
        
        existing.setCode(request.getCode());
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());        
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
    public PageResponse<CategoryTransaction> getAllWithFilter(
            int page,
            int size,
            String sortBy,
            String orderDir,
            String kode,
            String name
          
    ) {
        Sort.Direction direction = Sort.Direction.fromString(orderDir);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        
        Page<CategoryTransaction> resultPage;
        
        boolean hasName = StringUtils.hasText(name);
        boolean hasKode = StringUtils.hasText(kode);
        
       if (hasName && hasKode) {
           resultPage = repository.findByNameContainingIgnoreCaseAndCodeContainingIgnoreCase(kode, name, pageable);
        } 
        else if (hasName) {
            resultPage = repository.findByNameContainingIgnoreCase(name, pageable);
        } 
        else if (hasKode) {
            resultPage = repository.findByCodeContainingIgnoreCase(kode, pageable);
        } 
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
    public Optional<CategoryTransaction> getKategoriById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public CategoryTransaction saveKategori(CategoryTransaction kategori) {
        return repository.save(kategori);
    }
    

    @Override
    public CategoryTransaction updateKategori(CategoryTransaction kategori) {
        return repository.save(kategori);
    }
    
    @Override
    public void deleteKategori(Integer id) {
        repository.deleteById(id);
    }
}