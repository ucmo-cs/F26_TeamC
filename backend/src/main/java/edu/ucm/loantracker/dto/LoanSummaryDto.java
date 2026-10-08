package edu.ucm.loantracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One row of the admin's "open loans" table. */
public record LoanSummaryDto(
        Long id,
        String customerName,
        LocalDate startDate,
        BigDecimal remainingBalance,
        BigDecimal originalAmount,
        BigDecimal interestRate) {
}
