package com.dev.sales_api.controllers;

import com.dev.sales_api.models.Seller;
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
    public ResponseEntity<List<Seller>> findAll() {
        List<Seller> sellers = sellerService.findAll();
        return ResponseEntity.ok(sellers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> findById(@PathVariable Long id) {
        Seller seller = sellerService.findById(id);
        return ResponseEntity.ok(seller);
    }
}