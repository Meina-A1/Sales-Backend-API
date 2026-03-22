package com.dev.sales_api.services;

import com.dev.sales_api.dtos.SaleRequestDTO;
import com.dev.sales_api.dtos.SaleResponseDTO;
import com.dev.sales_api.dtos.SellerStatsResponseDTO;
import com.dev.sales_api.models.Sale;
import com.dev.sales_api.models.Seller;
import com.dev.sales_api.repositories.SaleRepository;
import com.dev.sales_api.repositories.SellerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SellerRepository sellerRepository;

    // Construtor com a injeção dos dois repositórios
    public SaleService(SaleRepository saleRepository, SellerRepository sellerRepository) {
        this.saleRepository = saleRepository;
        this.sellerRepository = sellerRepository;
    }

    // Método para criar uma venda
    public SaleResponseDTO createSale(SaleRequestDTO dto) {
        Seller seller = sellerRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendedor não encontrado com o ID: " + dto.getSellerId()));

        Sale sale = new Sale();
        sale.setSaleDate(LocalDate.now());
        sale.setAmount(dto.getAmount());
        sale.setSeller(seller);

        Sale saved = saleRepository.save(sale);

        return new SaleResponseDTO(
                saved.getId(),
                saved.getSaleDate(),
                saved.getAmount(),
                seller.getId(),
                seller.getName()
        );
    }

    public List<SellerStatsResponseDTO> getSellerStatistics(LocalDate startDate, LocalDate endDate) {
        // 1. Busca todas as vendas no período usando o repositório
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);

        // 2. Calcula a quantidade de dias no período (adicionando 1 para incluir o dia de início e fim)
        long daysInPeriod = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // Proteção essencial: garante que não haverá divisão por zero se as datas vierem incorretas
        if (daysInPeriod <= 0) {
            daysInPeriod = 1;
        }

        // Variável final para ser usada dentro do lambda
        final long finalDaysInPeriod = daysInPeriod;

        // 3. Agrupa as vendas pelo Vendedor (Seller)
        Map<Seller, List<Sale>> salesBySeller = sales.stream()
                .collect(Collectors.groupingBy(Sale::getSeller));

        // 4. Converte o mapa agrupado na nossa lista de DTOs
        return salesBySeller.entrySet().stream()
                .map(entry -> {
                    Seller seller = entry.getKey();
                    List<Sale> sellerSales = entry.getValue();

                    // Variável renomeada conforme o feedback
                    Long totalSales = (long) sellerSales.size();

                    // Soma o valor (amount) de todas as vendas desse vendedor
                    BigDecimal totalAmount = sellerSales.stream()
                            .map(Sale::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Calcula a média diária (Valor Total / Dias no Período)
                    BigDecimal dailyAverage = totalAmount.divide(BigDecimal.valueOf(finalDaysInPeriod), 2, RoundingMode.HALF_UP);

                    return new SellerStatsResponseDTO(
                            seller.getName(), 
                            totalSales,
                            dailyAverage
                    );
                })
                .collect(Collectors.toList());
    }
}