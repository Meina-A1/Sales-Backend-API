package com.dev.sales_api;

import com.dev.sales_api.models.Sale;
import com.dev.sales_api.models.Seller;
import com.dev.sales_api.repositories.SaleRepository;
import com.dev.sales_api.repositories.SellerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(SellerRepository sellerRepository, SaleRepository saleRepository) {
        return args -> {

            // Cria os vendedores
            Seller frieren = new Seller();
            frieren.setName("Frieren");
            sellerRepository.save(frieren);

            Seller aura = new Seller();
            aura.setName("Aura");
            sellerRepository.save(aura);

            // Datas de referência
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate twoDaysAgo = today.minusDays(2);

            // Cria as vendas
            Sale s1 = new Sale();
            s1.setSaleDate(today);
            s1.setAmount(new BigDecimal("500.00"));
            s1.setSeller(frieren);
            saleRepository.save(s1);

            Sale s2 = new Sale();
            s2.setSaleDate(yesterday);
            s2.setAmount(new BigDecimal("300.00"));
            s2.setSeller(aura);
            saleRepository.save(s2);

            Sale s3 = new Sale();
            s3.setSaleDate(twoDaysAgo);
            s3.setAmount(new BigDecimal("200.00"));
            s3.setSeller(frieren);
            saleRepository.save(s3);

            Sale s4 = new Sale();
            s4.setSaleDate(today);
            s4.setAmount(new BigDecimal("750.00"));
            s4.setSeller(frieren);
            saleRepository.save(s4);

            Sale s5 = new Sale();
            s5.setSaleDate(yesterday);
            s5.setAmount(new BigDecimal("250.00"));
            s5.setSeller(aura);
            saleRepository.save(s5);

            System.out.println("=== DataSeeder: 2 vendedores e 5 vendas criados com sucesso ===");
        };
    }
}