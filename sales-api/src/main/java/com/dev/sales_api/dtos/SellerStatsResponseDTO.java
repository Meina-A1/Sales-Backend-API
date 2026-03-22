package com.dev.sales_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerStatsResponseDTO {

    private String sellerName;
    private Long totalSales;
    private BigDecimal dailyAverage;
    
}