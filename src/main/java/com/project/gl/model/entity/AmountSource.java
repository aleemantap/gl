package com.project.gl.model.entity;

import java.util.List;

public final class AmountSource {

    public static final List<String> OPTIONS = List.of(
        "loan_amount",
        "repayment_amount",
        "interest_amount",
        "admin_cost",
        "provision",
        "customer_cash",
        "customer_bank",
        "principal_payment",
        "payment_amount",
        "down_payment",
        "financed_amount",
        "cost_amount",
        "margin_amount",
        "penalty_amount"
    );

    private AmountSource() {
    }
}