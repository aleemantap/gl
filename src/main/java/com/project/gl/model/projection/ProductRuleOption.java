package com.project.gl.model.projection;

import java.time.LocalDate;

public interface ProductRuleOption {

    Long getId();
    Long getProductId();
    String getProductCode();
    String getProductName();
    Integer getRuleVersion();
    LocalDate getEffectiveFrom();
}
