package com.dev.sales_api.controllers;

import com.dev.sales_api.models.Sale;
import com.dev.sales_api.models.Seller;
import com.dev.sales_api.repositories.SaleRepository;
import com.dev.sales_api.repositories.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SaleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SellerRepository sellerRepository;

    private Seller seller;

    @BeforeEach
    void setUp() {
        saleRepository.deleteAll();
        sellerRepository.deleteAll();

        seller = new Seller();
        seller.setName("Alice Silva");
        sellerRepository.save(seller);
    }

    // --- POST /api/sales ---

    @Test
    void createSale_shouldReturn201_withValidPayload() throws Exception {
        String json = """
                {
                    "amount": 500.00,
                    "sellerId": %d
                }
                """.formatted(seller.getId());

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.sellerName").value("Alice Silva"))
                .andExpect(jsonPath("$.sellerId").exists())
                .andExpect(jsonPath("$.saleDate").value(LocalDate.now().toString()));
    }

    @Test
    void createSale_shouldReturn400_whenAmountIsMissing() throws Exception {
        String json = """
                {
                    "sellerId": %d
                }
                """.formatted(seller.getId());

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSale_shouldReturn400_whenAmountIsZero() throws Exception {
        String json = """
                {
                    "amount": 0.00,
                    "sellerId": %d
                }
                """.formatted(seller.getId());

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSale_shouldReturn404_whenSellerDoesNotExist() throws Exception {
        String json = """
                {
                    "amount": 500.00,
                    "sellerId": 9999
                }
                """;

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // --- GET /api/sales/statistics ---

    @Test
    void getSellerStatistics_shouldReturn200_withCorrectStats() throws Exception {
        Sale sale = new Sale();
        sale.setSaleDate(LocalDate.now());
        sale.setAmount(new BigDecimal("300.00"));
        sale.setSeller(seller);
        saleRepository.save(sale);

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().toString();

        mockMvc.perform(get("/api/sales/statistics")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sellerName").value("Alice Silva"))
                .andExpect(jsonPath("$[0].totalSales").value(1))
                .andExpect(jsonPath("$[0].dailyAverage").value(300.00));
    }

    @Test
    void getSellerStatistics_shouldReturnEmptyList_whenNoSalesInPeriod() throws Exception {
        mockMvc.perform(get("/api/sales/statistics")
                        .param("startDate", "2020-01-01")
                        .param("endDate", "2020-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}