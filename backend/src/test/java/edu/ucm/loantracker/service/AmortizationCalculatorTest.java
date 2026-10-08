package edu.ucm.loantracker.service;

import edu.ucm.loantracker.model.Frequency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AmortizationCalculatorTest {

    private final AmortizationCalculator calc = new AmortizationCalculator();

    @Test
    void monthlyPaymentMatchesTextbookExample() {
        // $10,000 at 6% for 12 months = $860.66 (rounded up to $860.67 so the minimum always clears the loan)
        BigDecimal p = calc.minimumPayment(new BigDecimal("10000"), new BigDecimal("6"), 12, 12);
        assertEquals(new BigDecimal("860.67"), p);
    }

    @Test
    void zeroInterestSplitsBalanceEvenly() {
        BigDecimal p = calc.minimumPayment(new BigDecimal("1200"), BigDecimal.ZERO, 26, 24);
        assertEquals(new BigDecimal("50.00"), p);
    }

    @Test
    void biweeklyMinimumIsSmallerThanMonthly() {
        BigDecimal monthly = calc.minimumPayment(new BigDecimal("10000"), new BigDecimal("6"), 12, 12);
        BigDecimal biweekly = calc.minimumPayment(new BigDecimal("10000"), new BigDecimal("6"), 26, 26);
        assertTrue(biweekly.compareTo(monthly) < 0);
    }

    @Test
    void payoffDateWithNoInterest() {
        LocalDate start = LocalDate.of(2030, 1, 15);
        LocalDate payoff = calc.payoffDate(new BigDecimal("1000"), BigDecimal.ZERO, Frequency.MONTHLY,
                new BigDecimal("100"), start, LocalDate.of(2030, 1, 1));
        assertEquals(LocalDate.of(2030, 10, 15), payoff); // 10th payment
    }

    @Test
    void payoffIsNullWhenPaymentNeverCoversInterest() {
        LocalDate today = LocalDate.of(2030, 1, 1);
        assertNull(calc.payoffDate(new BigDecimal("100000"), new BigDecimal("12"), Frequency.MONTHLY,
                new BigDecimal("500"), today, today));
    }
}
