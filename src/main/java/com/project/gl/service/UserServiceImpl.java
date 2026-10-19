package com.project.gl.service;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.UserResponse;
// import com.project.gl.model.response.UserResponse2;
import com.project.gl.repo.UserRepository;
import com.project.gl.helper.Util;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
// import com.project.gl.model.response.UserResponse;
import com.project.gl.model.response.UserResponse2;




@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  Util util;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(int page, 
                                                int size, 
                                                String sortBy, 
                                                String direction, 
                                                String name,
                                                Integer userGroupId,
                                                String status,
                                                Integer cabang                                                        
                                                ) {
   
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Panggil repository dengan filter
        Page<User> pageB = userRepository.findAllWithFilters(
           name, userGroupId,  status, cabang,  pageable
        );
        
        return new PageResponse<>(
            pageB.getContent().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList()),
            pageB.getNumber(),
            pageB.getSize(),
            pageB.getTotalElements(),
            pageB.getTotalPages(),
            pageB.isLast()
        );
    }
 
    @Override
    public List<UserResponse2> findAllWithFiltersExport(
        String idsJson,
        String name, 
        Integer userGroupId,
        String status,
        Integer cabang  
    
       ) {

        List<Long> ids = util.parseIdsJson(idsJson);

        boolean idsEmpty = (ids == null || ids.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : ids; 

        List<User> rows =  userRepository.findAllWithFiltersExport(
                safeIds, 
                idsEmpty, 
                name,
                userGroupId,
                status,
                cabang
        );

       return rows.stream()
        .map(p -> UserResponse2.builder()
                .id(p.getId())
                .name(p.getName())
                .email(p.getEmail())
                .active(p.getActive())
                .cabangId(p.getCabang() != null
                        ? p.getCabang().getId()
                        : p.getCabangId())
                .cabangName(p.getCabang() != null
                        ? p.getCabang().getNamaCabang()
                        : null)
                .cabangKode(p.getCabang() != null
                        ? p.getCabang().getKodeCabang()
                        : null)
                .userGroupId(p.getUserGroup() != null
                        ? p.getUserGroup().getId() : null)
                .userGroup(p.getUserGroup() != null
                        ? p.getUserGroup().getName() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build())
        .collect(Collectors.toList());
    }

}
