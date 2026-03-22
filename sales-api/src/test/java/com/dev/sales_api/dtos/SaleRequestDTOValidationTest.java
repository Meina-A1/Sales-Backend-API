package com.dev.sales_api.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SaleRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // --- Valid payload ---

    @Test
    void shouldPassValidation_whenPayloadIsValid() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setSellerId(1L);

        Set<ConstraintViolation<SaleRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    // --- amount validations ---

    @Test
    void shouldFailValidation_whenAmountIsNull() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setAmount(null);
        dto.setSellerId(1L);

        Set<ConstraintViolation<SaleRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Amount is required");
    }

    @Test
    void shouldFailValidation_whenAmountIsZero() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setAmount(BigDecimal.ZERO);
        dto.setSellerId(1L);

        Set<ConstraintViolation<SaleRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Amount must be greater than zero");
    }

    @Test
    void shouldFailValidation_whenAmountIsNegative() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setAmount(new BigDecimal("-50.00"));
        dto.setSellerId(1L);

        Set<ConstraintViolation<SaleRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Amount must be greater than zero");
    }

    @Test
    void shouldPassValidation_whenAmountIsMinimumAllowed() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setAmount(new BigDecimal("0.01"));
        dto.setSellerId(1L);

        Set<ConstraintViolation<SaleRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    // --- sellerId validations ---

    @Test
    void shouldFailValidation_whenSellerIdIsNull() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setSellerId(null);

        Set<ConstraintViolation<SaleRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Seller ID is required");
    }

    // --- Multiple violations ---

    @Test
    void shouldReturnMultipleViolations_whenBothFieldsAreInvalid() {
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setAmount(null);
        dto.setSellerId(null);

        Set<ConstraintViolation<SaleRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);
    }
}