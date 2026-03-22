package com.dev.sales_api.repositories;

import com.dev.sales_api.models.Sale;
import com.dev.sales_api.models.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SaleRepositoryTest {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SellerRepository sellerRepository;

    private Seller seller;

    private final LocalDate TODAY = LocalDate.now();
    private final LocalDate YESTERDAY = TODAY.minusDays(1);
    private final LocalDate TWO_DAYS_AGO = TODAY.minusDays(2);
    private final LocalDate LAST_WEEK = TODAY.minusDays(7);

    @BeforeEach
    void setUp() {
        saleRepository.deleteAll();
        sellerRepository.deleteAll();

        seller = new Seller();
        seller.setName("Alice Silva");
        sellerRepository.save(seller);

        // Sale inside range
        Sale s1 = new Sale();
        s1.setSaleDate(TODAY);
        s1.setAmount(new BigDecimal("100.00"));
        s1.setSeller(seller);
        saleRepository.save(s1);

        // Sale inside range
        Sale s2 = new Sale();
        s2.setSaleDate(YESTERDAY);
        s2.setAmount(new BigDecimal("200.00"));
        s2.setSeller(seller);
        saleRepository.save(s2);

        // Sale inside range
        Sale s3 = new Sale();
        s3.setSaleDate(TWO_DAYS_AGO);
        s3.setAmount(new BigDecimal("300.00"));
        s3.setSeller(seller);
        saleRepository.save(s3);

        // Sale OUTSIDE range — should never appear in results
        Sale s4 = new Sale();
        s4.setSaleDate(LAST_WEEK);
        s4.setAmount(new BigDecimal("999.00"));
        s4.setSeller(seller);
        saleRepository.save(s4);
    }

    @Test
    void findBySaleDateBetween_shouldReturnSalesWithinRange() {
        List<Sale> result = saleRepository.findBySaleDateBetween(TWO_DAYS_AGO, TODAY);

        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(Sale::getAmount)
                .containsExactlyInAnyOrder(
                        new BigDecimal("100.00"),
                        new BigDecimal("200.00"),
                        new BigDecimal("300.00")
                );
    }

    @Test
    void findBySaleDateBetween_shouldExcludeSalesOutsideRange() {
        List<Sale> result = saleRepository.findBySaleDateBetween(TWO_DAYS_AGO, TODAY);

        assertThat(result)
                .extracting(Sale::getAmount)
                .doesNotContain(new BigDecimal("999.00"));
    }

    @Test
    void findBySaleDateBetween_shouldReturnEmpty_whenNoSalesInPeriod() {
        LocalDate farPast = LocalDate.of(2000, 1, 1);
        LocalDate farPastEnd = LocalDate.of(2000, 1, 31);

        List<Sale> result = saleRepository.findBySaleDateBetween(farPast, farPastEnd);

        assertThat(result).isEmpty();
    }

    @Test
    void findBySaleDateBetween_shouldReturnOneSale_whenStartDateEqualsEndDate() {
        List<Sale> result = saleRepository.findBySaleDateBetween(TODAY, TODAY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void findBySaleDateBetween_shouldIncludeBoundaryDates() {
        // Verifies the query is inclusive on both ends (BETWEEN is inclusive in SQL)
        List<Sale> result = saleRepository.findBySaleDateBetween(TWO_DAYS_AGO, TWO_DAYS_AGO);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo("300.00");
    }
}