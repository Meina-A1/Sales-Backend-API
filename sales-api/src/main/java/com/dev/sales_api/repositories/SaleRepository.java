package com.dev.sales_api.repositories;

import com.dev.sales_api.models.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    
}