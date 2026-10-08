package edu.ucm.loantracker.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateLoanRequest(
        @NotBlank String customerName,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotNull @PastOrPresent LocalDate startDate,
        @NotNull @Positive BigDecimal originalAmount,
        @NotNull @PositiveOrZero @DecimalMax("100") BigDecimal interestRate,
        @Positive Integer termMonths) {   // optional; falls back to app.loan.default-term-months
}
