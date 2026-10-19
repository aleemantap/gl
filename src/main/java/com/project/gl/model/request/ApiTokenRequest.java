package com.project.gl.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiTokenRequest {

    private String clientId;

    private String clientSecret;
}