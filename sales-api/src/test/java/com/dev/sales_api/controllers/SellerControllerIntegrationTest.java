package com.dev.sales_api.controllers;

import com.dev.sales_api.models.Seller;
import com.dev.sales_api.repositories.SaleRepository;
import com.dev.sales_api.repositories.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SellerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private SaleRepository saleRepository;

    private Seller seller;

    @BeforeEach
    void setUp() {
        saleRepository.deleteAll();
        sellerRepository.deleteAll();

        seller = new Seller();
        seller.setName("Alice Silva");
        sellerRepository.save(seller);
    }

    // --- GET /api/sellers ---

    @Test
    void findAll_shouldReturn200_withListOfSellers() throws Exception {
        Seller bob = new Seller();
        bob.setName("Bob Souza");
        sellerRepository.save(bob);

        mockMvc.perform(get("/api/sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alice Silva", "Bob Souza")));
    }

    @Test
    void findAll_shouldReturn200_withEmptyList_whenNoSellersExist() throws Exception {
        saleRepository.deleteAll();
        sellerRepository.deleteAll();

        mockMvc.perform(get("/api/sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- GET /api/sellers/{id} ---

    @Test
    void findById_shouldReturn200_withSeller_whenIdExists() throws Exception {
        mockMvc.perform(get("/api/sellers/{id}", seller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seller.getId()))
                .andExpect(jsonPath("$.name").value("Alice Silva"));
    }

    @Test
    void findById_shouldReturn404_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/sellers/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}