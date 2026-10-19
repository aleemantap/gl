package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JournalCodeOptionResponse {

    private Long id;
    private String journalCode;
    private String description;
}