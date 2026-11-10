package com.project.gl.service;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.User;
import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.request.UserRequest;
import com.project.gl.repo.GroupRepository;
import com.project.gl.repo.CabangRepository;

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
    private GroupRepository userGroupRepository;

    @Autowired
    private CabangRepository cabangRepository;

    @Autowired
    private PasswordEncoder  passwordEncoder;

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

    @Override
    public UserResponse saveUser(UserRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }


        // Get references
        UserGroup userGroup = userGroupRepository.findById(request.getGroup())
                .orElseThrow(() -> new RuntimeException("User group not found with id: " + request.getGroup()));
        
         // Create new user
        User user = new User();

        Cabang cabang = null;
        if (request.getCabang() != null && request.getCabang() > 0) {
            cabang = cabangRepository.findById(request.getCabang().intValue())
                    .orElseThrow(() -> new RuntimeException("Cabang not found with id: " + request.getCabang()));
            user.setCabangId(request.getCabang().intValue());
        }
        
       
       
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // user.setUserGroup(userGroup);   
        user.setUserGroupId(request.getGroup().intValue());          
        user.setActive(request.getActive() != null ? request.getActive() : "Y");
        // Save
        User savedUser = userRepository.save(user);

        // Convert to response using existing constructor
        return new UserResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(UserRequest request) {
        // Validate user exists
        User existingUser = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getId()));

        // Validate email uniqueness (exclude current user)
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), request.getId())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Get references
        UserGroup userGroup = userGroupRepository.findById(request.getGroup())
                .orElseThrow(() -> new RuntimeException("User group not found with id: " + request.getGroup()));
        
        Cabang cabang = null;
        if (request.getCabang() != null && request.getCabang() > 0) {
            cabang = cabangRepository.findById(request.getCabang().intValue())
                    .orElseThrow(() -> new RuntimeException("Cabang not found with id: " + request.getCabang()));
            existingUser.setCabangId(request.getCabang().intValue());
        }
       
        // Update user
        existingUser.setEmail(request.getEmail());
        existingUser.setName(request.getName());
        
        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

          // Update avatar jika ada
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            existingUser.setAvatar(request.getAvatar());
        }

        
        // existingUser.setUserGroup(request.getGroup());
        existingUser.setUserGroupId(request.getGroup().intValue());
       
        
        // existingUser.setC
        // System.out.println("customer "+customer);
        // existingUser.setVendor(vendor);
        existingUser.setActive(request.getActive() != null ? request.getActive() : "Y");

        // Save
        User updatedUser = userRepository.save(existingUser);

        // return convertToResponse(updatedUser);
        return new UserResponse(updatedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return new UserResponse(user);
    }

 
    /**
     * Menonaktifkan user (soft delete) - mengubah active menjadi 'N'
     */
    @Override
    public UserResponse nonActiveUser(Long id) {
        // Cari user berdasarkan ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Cek apakah sudah non-aktif
        if ("N".equals(user.getActive())) {
            throw new RuntimeException("User already inactive");
        }
        
        // Ubah status menjadi non-aktif
        user.setActive("N");
        
        // Save perubahan
        User updatedUser = userRepository.save(user);
        
        return new UserResponse(updatedUser);
    }

    /**
     * Mengaktifkan kembali user - mengubah active menjadi 'Y'
     */
    @Override
    public UserResponse activeUser(Long id) {
        // Cari user berdasarkan ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Cek apakah sudah aktif
        if ("Y".equals(user.getActive())) {
            throw new RuntimeException("User already active");
        }
        
        // Ubah status menjadi aktif
        user.setActive("Y");
        
        // Save perubahan
        User updatedUser = userRepository.save(user);
        
        return new UserResponse(updatedUser);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Cek password lama
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        
        // Validasi password baru
        if (newPassword.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters");
        }
        
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("New password cannot be the same as old password");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
