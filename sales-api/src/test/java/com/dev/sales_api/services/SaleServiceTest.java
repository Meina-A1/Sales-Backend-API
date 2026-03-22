package com.dev.sales_api.services;

import com.dev.sales_api.dtos.SaleRequestDTO;
import com.dev.sales_api.dtos.SaleResponseDTO;
import com.dev.sales_api.dtos.SellerStatsResponseDTO;
import com.dev.sales_api.models.Sale;
import com.dev.sales_api.models.Seller;
import com.dev.sales_api.repositories.SaleRepository;
import com.dev.sales_api.repositories.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SaleService saleService;

    private Seller seller;

    @BeforeEach
    void setUp() {
        seller = new Seller();
        seller.setId(1L);
        seller.setName("Alice Silva");
    }

    // --- createSale ---

    @Test
    void createSale_shouldReturnSavedSale_whenSellerExists() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setSellerId(1L);
        dto.setAmount(new BigDecimal("500.00"));

        Sale savedSale = new Sale();
        savedSale.setId(1L);
        savedSale.setSaleDate(LocalDate.now());
        savedSale.setAmount(dto.getAmount());
        savedSale.setSeller(seller);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(saleRepository.save(any(Sale.class))).thenReturn(savedSale);

        SaleResponseDTO result = saleService.createSale(dto);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualByComparingTo("500.00");
        assertThat(result.getSellerName()).isEqualTo("Alice Silva");
        assertThat(result.getSaleDate()).isEqualTo(LocalDate.now());
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    void createSale_shouldThrow404_whenSellerNotFound() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setSellerId(99L);
        dto.setAmount(new BigDecimal("500.00"));

        when(sellerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.createSale(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");

        verify(saleRepository, never()).save(any());
    }

    // --- getSellerStatistics ---

    @Test
    void getSellerStatistics_shouldReturnCorrectStats_forOneSeller() {
        LocalDate start = LocalDate.now().minusDays(2);
        LocalDate end = LocalDate.now(); // 3 days in period

        Sale sale1 = new Sale();
        sale1.setAmount(new BigDecimal("300.00"));
        sale1.setSaleDate(start);
        sale1.setSeller(seller);

        Sale sale2 = new Sale();
        sale2.setAmount(new BigDecimal("600.00"));
        sale2.setSaleDate(end);
        sale2.setSeller(seller);

        when(saleRepository.findBySaleDateBetween(start, end)).thenReturn(List.of(sale1, sale2));

        List<SellerStatsResponseDTO> result = saleService.getSellerStatistics(start, end);

        assertThat(result).hasSize(1);
        SellerStatsResponseDTO stats = result.get(0);
        assertThat(stats.getSellerName()).isEqualTo("Alice Silva");
        assertThat(stats.getTotalSales()).isEqualTo(2L);
        // Total: 900.00 / 3 days = 300.00
        assertThat(stats.getDailyAverage()).isEqualByComparingTo("300.00");
    }

    @Test
    void getSellerStatistics_shouldReturnEmptyList_whenNoSalesInPeriod() {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2020, 1, 31);

        when(saleRepository.findBySaleDateBetween(start, end)).thenReturn(List.of());

        List<SellerStatsResponseDTO> result = saleService.getSellerStatistics(start, end);

        assertThat(result).isEmpty();
    }

    @Test
    void getSellerStatistics_shouldGroupCorrectly_forMultipleSellers() {
        Seller bob = new Seller();
        bob.setId(2L);
        bob.setName("Bob Souza");

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now(); // 2 days

        Sale aliceSale = new Sale();
        aliceSale.setAmount(new BigDecimal("200.00"));
        aliceSale.setSaleDate(start);
        aliceSale.setSeller(seller);

        Sale bobSale = new Sale();
        bobSale.setAmount(new BigDecimal("400.00"));
        bobSale.setSaleDate(end);
        bobSale.setSeller(bob);

        when(saleRepository.findBySaleDateBetween(start, end)).thenReturn(List.of(aliceSale, bobSale));

        List<SellerStatsResponseDTO> result = saleService.getSellerStatistics(start, end);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(SellerStatsResponseDTO::getSellerName)
                .containsExactlyInAnyOrder("Alice Silva", "Bob Souza");
    }
}