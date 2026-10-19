package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiTokenResponse {

    private String accessToken;

    private String tokenType;

    private long expiresIn;
}