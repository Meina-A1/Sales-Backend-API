# Sales Backend API

API REST desenvolvida em Spring Boot para gerenciamento de vendas, criada como parte de um processo seletivo.

## Tecnologias

- Java 17
- Spring Boot 3.5.12
- Spring Data JPA
- Spring Validation
- H2 Database (in-memory)
- Lombok
- JUnit 5 + Mockito

## Como executar

### Pré-requisitos
- Java 17+
- Maven (ou use o `mvnw` incluso no projeto)

### Rodando a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

### Rodando os testes

```bash
./mvnw test
```

> 32 testes automatizados — 0 falhas.

### H2 Console

Com a aplicação rodando, acesse o banco de dados em memória pelo browser:

```
URL:      http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (deixar em branco)
```

Ao iniciar, o `DataSeeder` popula automaticamente o banco com 2 vendedores e 5 vendas para facilitar os testes manuais.

## Endpoints

### Vendas

#### Criar uma nova venda
```
POST /api/sales
```

**Body:**
```json
{
    "amount": 1500.00,
    "sellerId": 1
}
```

**Resposta (201 Created):**
```json
{
    "id": 6,
    "saleDate": "2026-03-22",
    "amount": 1500.00,
    "sellerId": 1,
    "sellerName": "Frieren"
}
```

#### Estatísticas de vendedores por período
```
GET /api/sales/statistics?startDate={startDate}&endDate={endDate}
```

**Exemplo:**
```
GET /api/sales/statistics?startDate=2026-01-01&endDate=2026-03-22
```

**Resposta (200 OK):**
```json
[
    {
        "sellerName": "Frieren",
        "totalSales": 3,
        "dailyAverage": 17.90
    },
    {
        "sellerName": "Aura",
        "totalSales": 2,
        "dailyAverage": 6.79
    }
]
```

### Vendedores

#### Listar todos os vendedores
```
GET /api/sellers
```

**Resposta (200 OK):**
```json
[
    { "id": 1, "name": "Frieren" },
    { "id": 2, "name": "Aura" }
]
```

#### Buscar vendedor por ID
```
GET /api/sellers/{id}
```

**Resposta (200 OK):**
```json
{ "id": 1, "name": "Frieren" }
```

## Testando os endpoints

### Linux / Mac

```bash
# Criar uma venda
curl -X POST http://localhost:8080/api/sales \
  -H "Content-Type: application/json" \
  -d '{"amount": 1500.00, "sellerId": 1}'

# Estatísticas por período
curl "http://localhost:8080/api/sales/statistics?startDate=2026-01-01&endDate=2026-03-22"

# Listar vendedores
curl http://localhost:8080/api/sellers

# Buscar vendedor por ID
curl http://localhost:8080/api/sellers/1
```

### Windows — CMD

```cmd
:: Criar uma venda
curl -X POST http://localhost:8080/api/sales -H "Content-Type: application/json" -d "{\"amount\": 1500.00, \"sellerId\": 1}"

:: Estatísticas por período
curl "http://localhost:8080/api/sales/statistics?startDate=2026-01-01&endDate=2026-03-22"

:: Listar vendedores
curl http://localhost:8080/api/sellers

:: Buscar vendedor por ID
curl http://localhost:8080/api/sellers/1
```

### Windows — PowerShell

```powershell
# Criar uma venda
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/api/sales" -ContentType "application/json" -Body '{"amount": 1500.00, "sellerId": 1}'

# Estatísticas por período
Invoke-RestMethod -Uri "http://localhost:8080/api/sales/statistics?startDate=2026-01-01&endDate=2026-03-22"

# Listar vendedores
Invoke-RestMethod -Uri "http://localhost:8080/api/sellers"

# Buscar vendedor por ID
Invoke-RestMethod -Uri "http://localhost:8080/api/sellers/1"
```

## Estrutura do Projeto

```
src/
├── main/java/com/dev/sales_api/
│   ├── controllers/
│   │   ├── SaleController.java
│   │   └── SellerController.java
│   ├── dtos/
│   │   ├── SaleRequestDTO.java
│   │   ├── SaleResponseDTO.java
│   │   ├── SellerResponseDTO.java
│   │   └── SellerStatsResponseDTO.java
│   ├── models/
│   │   ├── Sale.java
│   │   └── Seller.java
│   ├── repositories/
│   │   ├── SaleRepository.java
│   │   └── SellerRepository.java
│   ├── services/
│   │   ├── SaleService.java
│   │   └── SellerService.java
│   └── DataSeeder.java
└── test/java/com/dev/sales_api/
    ├── controllers/
    │   ├── SaleControllerIntegrationTest.java
    │   └── SellerControllerIntegrationTest.java
    ├── dtos/
    │   └── SaleRequestDTOValidationTest.java
    ├── repositories/
    │   └── SaleRepositoryTest.java
    └── services/
        ├── SaleServiceTest.java
        └── SellerServiceTest.java
```

## Decisões Arquiteturais

- **Separação entre Entity e DTO:** As entidades JPA (`Sale`, `Seller`) são mantidas exclusivamente para mapeamento do banco de dados. Os DTOs controlam o que entra e sai da API, evitando exposição desnecessária do modelo de dados.
- **Banco de dados normalizado:** O nome do vendedor não é armazenado redundantemente na tabela de vendas. A relação `@ManyToOne` entre `Sale` e `Seller` garante integridade referencial, e o `SaleResponseDTO` achata a resposta conforme exigido pelo desafio.
- **DataSeeder:** Ao iniciar a aplicação, 2 vendedores e 5 vendas são criados automaticamente para facilitar testes manuais via H2 Console ou curl.
