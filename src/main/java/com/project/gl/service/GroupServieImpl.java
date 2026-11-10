package com.project.gl.service;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.User;
import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.request.UserRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.UserResponse;
import com.project.gl.model.response.GroupResponse;
import com.project.gl.repo.GroupRepository;
import com.project.gl.model.request.GroupRequest;
import com.project.gl.repo.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public GroupResponse getGroupById(Long id) {
        UserGroup g = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return new GroupResponse(g);
    }

    @Override
    public GroupResponse saveGroup(GroupRequest request) {
        // Validate email uniqueness
        if (groupRepository.existsByName(request.getNamaGroup())) {
            throw new RuntimeException("Nama group already exists: " + request.getNamaGroup());
        }


        
         // Create new user
        UserGroup g = new UserGroup();
        g.setName(request.getNamaGroup());  
        g.setDescription(request.getKeterangan());
        // user.setUserGroup(userGroup);   
        g.setActive(request.getActive() != null ? request.getActive() : "Y");
        // Save
        UserGroup saved = groupRepository.save(g);
        // Convert to response using existing constructor
        return new GroupResponse(saved);
    }

    @Override
    public GroupResponse updateGroup(GroupRequest request) {
        // Validate user exists
        UserGroup existing = groupRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + request.getId()));

        // Update user
        existing.setName(request.getNamaGroup());
        existing.setDescription(request.getKeterangan());
        existing.setActive(request.getActive() != null ? request.getActive() : "Y");
        // Save
        UserGroup updated = groupRepository.save(existing);
        // return convertToResponse(updatedUser);
        return new GroupResponse(updated);
    }

    @Override
    public void delete(Long id) {
        // Cek dulu apakah data ada
        UserGroup group = groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group dengan id " + id + " tidak ditemukan"));
        // (Opsional) cek apakah group masih dipakai user
        long userCount = userRepository.countByUserGroupId(id);
        if (userCount > 0) {
            throw new RuntimeException("Group tidak bisa dihapus karena masih dipakai oleh " + userCount + " user");
        }
        groupRepository.delete(group);
    }

    
}
