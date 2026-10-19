package com.project.gl.service;

import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
public interface NomorGel {
    
     PageResponse<NomorGelAccountResponse> getAlls(int page, 
                                                int size, 
                                                String sortBy, 
                                                String direction, 
                                                String name                            
                                            );
}

