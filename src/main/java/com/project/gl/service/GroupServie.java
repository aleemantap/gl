package com.project.gl.service;

import java.util.List;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.request.GroupRequest;
import com.project.gl.model.response.GroupResponse;


public interface GroupServie {
    PageResponse<GroupResponse> getAllGroups(int page, 
                                                int size, 
                                                String sortBy, 
                                                String direction, 
                                                String name                                                                                       
                                            );
    List<UserGroup> getAll();   
    GroupResponse getGroupById(Long id);
    void delete(Long id);
    GroupResponse saveGroup(GroupRequest request);
    GroupResponse updateGroup(GroupRequest request);           
}
