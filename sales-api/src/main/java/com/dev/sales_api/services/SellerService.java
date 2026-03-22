package com.dev.sales_api.services;

import com.dev.sales_api.dtos.SellerResponseDTO;
import com.dev.sales_api.models.Seller;
import com.dev.sales_api.repositories.SellerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    // Injeção de dependência via construtor
    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public List<SellerResponseDTO> findAll() {
    return sellerRepository.findAll()
            .stream()
            .map(s -> new SellerResponseDTO(s.getId(), s.getName()))
            .collect(java.util.stream.Collectors.toList());
    }

    public SellerResponseDTO findById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendedor não encontrado com o ID: " + id));
        return new SellerResponseDTO(seller.getId(), seller.getName());
    }
}