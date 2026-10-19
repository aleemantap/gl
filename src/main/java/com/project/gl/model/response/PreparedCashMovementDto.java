package com.project.gl.model.response;

import java.math.BigDecimal;
import com.project.gl.model.entity.ProductRuleComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreparedCashMovementDto {

    private ProductRuleComponent component;
    private String direction;
    private BigDecimal amount;
}