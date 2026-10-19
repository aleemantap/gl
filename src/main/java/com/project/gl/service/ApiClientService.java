package com.project.gl.service;


import java.util.List;
import com.project.gl.model.entity.ApiClient;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.ApiResponse;
import com.project.gl.model.response.ClientKeyResponse;
import com.project.gl.model.response.ApiClientResponse;


public interface ApiClientService {

    PageResponse<ApiClientResponse> getAllApiClient(
                                                int page, 
                                                int size, 
                                                String sortBy, 
                                                String direction, 
                                                String client_name,
                                                String client_id,
                                                Boolean active                                                                                     
                                            );
    //createApi
    ClientKeyResponse createApi(ApiClient request);

    ClientKeyResponse regenerate(Long id);

    void aktifNonAktif(Long id, String type);

    void update(Long id, ApiClient request);

    ApiClient findById(Long id);

  
}
