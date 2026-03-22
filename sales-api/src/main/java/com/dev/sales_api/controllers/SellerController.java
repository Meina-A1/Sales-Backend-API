package com.dev.sales_api.controllers;

import com.dev.sales_api.dtos.SellerResponseDTO;
import com.dev.sales_api.services.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public ResponseEntity<List<SellerResponseDTO>> findAll() {
        return ResponseEntity.ok(sellerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerService.findById(id));
    }
}