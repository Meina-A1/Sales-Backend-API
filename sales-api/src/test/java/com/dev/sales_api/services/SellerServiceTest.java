package com.dev.sales_api.services;

import com.dev.sales_api.models.Seller;
import com.dev.sales_api.repositories.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private Seller seller;

    @BeforeEach
    void setUp() {
        seller = new Seller();
        seller.setId(1L);
        seller.setName("Alice Silva");
    }

    // --- findAll ---

    @Test
    void findAll_shouldReturnAllSellers() {
        Seller bob = new Seller();
        bob.setId(2L);
        bob.setName("Bob Souza");

        when(sellerRepository.findAll()).thenReturn(List.of(seller, bob));

        List<Seller> result = sellerService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Seller::getName)
                .containsExactlyInAnyOrder("Alice Silva", "Bob Souza");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoSellersExist() {
        when(sellerRepository.findAll()).thenReturn(List.of());

        List<Seller> result = sellerService.findAll();

        assertThat(result).isEmpty();
    }

    // --- findById ---

    @Test
    void findById_shouldReturnSeller_whenIdExists() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        Seller result = sellerService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice Silva");
    }

    @Test
    void findById_shouldThrow404_whenIdNotFound() {
        when(sellerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellerService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");
    }
}