package com.project.gl.service;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.User;
import com.project.gl.model.entity.UserGroup;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.GroupResponse;
import com.project.gl.repo.GroupRepository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;



@Service
@Transactional
public class GroupServieImpl implements GroupServie {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GroupResponse> getAllGroups(int page, 
                                                int size, 
                                                String sortBy, 
                                                String direction, 
                                                String name
                                                ) {

        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Panggil repository dengan filter
        Page<UserGroup> pageB = groupRepository.findAllWithFilters(
           name, pageable
        );
        
        return new PageResponse<>(
            pageB.getContent().stream()
                .map(GroupResponse::new)
                .collect(Collectors.toList()),
            pageB.getNumber(),
            pageB.getSize(),
            pageB.getTotalElements(),
            pageB.getTotalPages(),
            pageB.isLast()
        );
    }

    @Override
    public List<UserGroup> getAll() {
        return groupRepository.findAll();
    }

    
}
