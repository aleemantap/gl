package com.project.gl.model.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private Long id;
    private String email;
    private String name;
    private String password;
    private Long group;
    private Long cabang;
    private String active;
    private String avatar;
}