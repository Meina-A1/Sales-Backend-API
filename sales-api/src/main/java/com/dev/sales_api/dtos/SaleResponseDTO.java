package com.dev.sales_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    private Long id;
    private LocalDate saleDate;
    private BigDecimal amount;
    private Long sellerId;
    private String sellerName;
}