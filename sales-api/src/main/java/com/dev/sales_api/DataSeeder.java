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
            Seller alice = new Seller();
            alice.setName("Alice Silva");
            sellerRepository.save(alice);

            Seller bob = new Seller();
            bob.setName("Bob Souza");
            sellerRepository.save(bob);

            // Datas de referência
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate twoDaysAgo = today.minusDays(2);

            // Vendas da Alice
            Sale s1 = new Sale();
            s1.setSaleDate(today);
            s1.setAmount(new BigDecimal("500.00"));
            s1.setSeller(alice);
            saleRepository.save(s1);

            Sale s2 = new Sale();
            s2.setSaleDate(yesterday);
            s2.setAmount(new BigDecimal("300.00"));
            s2.setSeller(alice);
            saleRepository.save(s2);

            Sale s3 = new Sale();
            s3.setSaleDate(twoDaysAgo);
            s3.setAmount(new BigDecimal("200.00"));
            s3.setSeller(alice);
            saleRepository.save(s3);

            // Vendas do Bob
            Sale s4 = new Sale();
            s4.setSaleDate(today);
            s4.setAmount(new BigDecimal("750.00"));
            s4.setSeller(bob);
            saleRepository.save(s4);

            Sale s5 = new Sale();
            s5.setSaleDate(yesterday);
            s5.setAmount(new BigDecimal("250.00"));
            s5.setSeller(bob);
            saleRepository.save(s5);

            System.out.println("=== DataSeeder: 2 vendedores e 5 vendas criados com sucesso ===");
        };
    }
}