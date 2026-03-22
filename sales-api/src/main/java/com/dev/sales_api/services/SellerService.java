package com.dev.sales_api.services;

import com.dev.sales_api.models.Seller;
import com.dev.sales_api.repositories.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    // Injeção de dependência via construtor
    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public List<Seller> findAll() {
        return sellerRepository.findAll();
    }

    public Seller findById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado com o ID: " + id));
    }
}