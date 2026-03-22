package com.dev.sales_api.controllers;

import com.dev.sales_api.dtos.SaleRequestDTO;
import com.dev.sales_api.dtos.SellerStatsResponseDTO;
import com.dev.sales_api.models.Sale;
import com.dev.sales_api.services.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<Sale> createSale(@Valid @RequestBody SaleRequestDTO dto) {
        Sale newSale = saleService.createSale(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSale);
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<SellerStatsResponseDTO>> getSellerStatistics(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        
        List<SellerStatsResponseDTO> statistics = saleService.getSellerStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}