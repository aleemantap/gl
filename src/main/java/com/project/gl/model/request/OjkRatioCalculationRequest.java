package com.project.gl.model.request;

import java.time.LocalDate;

import com.project.gl.model.enums.OjkRatioRegion;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OjkRatioCalculationRequest {

    @NotNull
    private LocalDate periode;

    @NotNull
    private OjkRatioRegion regionScope;
}