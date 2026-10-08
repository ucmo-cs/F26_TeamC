package edu.ucm.loantracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Everything about one loan. Used by the admin loan page and the customer dashboard. */
public record LoanDetailDto(
        Long id,
        Long customerId,
        String customerName,
        String email,
        String phone,
        LocalDate startDate,
        BigDecimal remainingBalance,
        BigDecimal originalAmount,
        BigDecimal interestRate,
        Integer termMonths,
        String bankName,
        String routingNumber,
        String accountNumber,
        List<ScheduleDto> schedules,
        LocalDate payoffDate,             // null when no active automatic payments
        BigDecimal minimumMonthlyPayment) {
}
