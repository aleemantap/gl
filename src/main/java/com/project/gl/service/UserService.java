package com.project.gl.service;


import java.util.List;
// import com.project.emcu.gmeds.model.entity.UserGroup;
// import com.project.emcu.gmeds.model.request.UserRequest;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.UserResponse;
// import com.project.emcu.gmeds.model.response.UserGroupResponse;
import com.project.gl.model.response.UserResponse2;


public interface UserService {

  
    // UserResponse saveUser(UserRequest request);
    // UserResponse updateUser(UserRequest request);
    PageResponse<UserResponse> getAllUsers(int page, 
                                                int size, 
                                                String sortBy, 
                                                String direction, 
                                                String name,
                                                Integer userGroupId,
                                                String status,
                                                Integer cabang                                                                                       
                                            );
    List<UserResponse2> findAllWithFiltersExport(
                                                String idsJson,
                                                String name,
                                                Integer userGroupId,
                                                String status,
                                                Integer cabang                                                                                       
                                            );

                                            
//     PageResponse<UserGroupResponse> getAllUserGroup(int page, 
//                                                 int size, 
//                                                 String sortBy, 
//                                                 String direction
                                                                                          
//                                             );
       
//    UserResponse getUserById(Long id);
//    UserResponse nonActiveUser(Long id); 
//    UserResponse activeUser(Long id);    
}
