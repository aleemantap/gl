package com.project.gl.helper;
import java.util.List;   
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Util {

    private final ObjectMapper objectMapper;
    
    public List<Long> parseIdsJson(String idsJson) {
        if (idsJson == null || idsJson.trim().isEmpty()) {
            return null;
        }
        try {
                List<Long> ids = objectMapper.readValue(idsJson, new TypeReference<List<Long>>() {});
                return (ids != null && !ids.isEmpty()) ? ids : null;
        } catch (Exception e) {
                return null;
        }
   }
}
