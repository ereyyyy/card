package com.card.card.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryResponse {
    private String userName;
    private List<String> products;
    private Double totalAmount;
    private Integer itemCount;
} 