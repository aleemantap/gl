package com.project.gl.service;

import java.util.List;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.UserGroup;

import com.project.gl.model.response.GroupResponse;


public interface GroupServie {
    PageResponse<GroupResponse> getAllGroups(int page, 
                                                int size, 
                                                String sortBy, 
                                                String direction, 
                                                String name                                                                                       
                                            );
//     PageResponse<UserGroupResponse> getAllUserGroup(int page, 
//                                                 int size, 
//                                                 String sortBy, 
//                                                 String direction
                                                                                          
//                                             );
    List<UserGroup> getAll();   
//    UserResponse getUserById(Long id);
//    UserResponse nonActiveUser(Long id); 
//    UserResponse activeUser(Long id);    
}
