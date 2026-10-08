package edu.ucm.loantracker.service;

import edu.ucm.loantracker.dto.MinimumPaymentDto;
import edu.ucm.loantracker.model.Frequency;
import edu.ucm.loantracker.model.Loan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Standard Amortization Formula:  P = r * B / (1 - (1 + r)^-n)
 *   B = balance still owed
 *   r = annual rate / payments per year  (12 monthly, 26 bi-weekly, 52 weekly)
 *   n = number of payments left in the loan term
 */
@Component
public class AmortizationCalculator {

    /** Safety cap so a payment smaller than the interest can't loop forever (~96 years of weekly payments). */
    private static final int MAX_PERIODS = 5000;

    public BigDecimal minimumPayment(BigDecimal balance, BigDecimal annualRatePercent, int paymentsPerYear, int paymentsRemaining) {
        if (balance == null || balance.signum() <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        double b = balance.doubleValue();
        double r = annualRatePercent.doubleValue() / 100.0 / paymentsPerYear;
        int n = Math.max(1, paymentsRemaining);
        double p = (r == 0) ? b / n : (r * b) / (1 - Math.pow(1 + r, -n));
        // Round up so paying the minimum always clears the loan within the term.
        return BigDecimal.valueOf(p).setScale(2, RoundingMode.UP);
    }

    /** Months left in the term, never less than 1 (overdue loans must be paid within a month). */
    public int remainingMonths(Loan loan, LocalDate today) {
        long elapsed = Math.max(0, ChronoUnit.MONTHS.between(loan.getStartDate(), today));
        return (int) Math.max(1, loan.getTermMonths() - elapsed);
    }

    public int paymentsRemaining(Loan loan, Frequency frequency, LocalDate today) {
        return (int) Math.ceil(remainingMonths(loan, today) * frequency.getPaymentsPerYear() / 12.0);
    }

    /** Minimum payment for this loan's remaining balance and remaining term, for the given schedule type. */
    public MinimumPaymentDto minimumFor(Loan loan, Frequency frequency, LocalDate today) {
        int n = paymentsRemaining(loan, frequency, today);
        BigDecimal min = minimumPayment(loan.getRemainingBalance(), loan.getInterestRate(), frequency.getPaymentsPerYear(), n);
        return new MinimumPaymentDto(frequency, frequency.getPaymentsPerYear(), n, min);
    }

    /**
     * Steps through future scheduled payments: each period adds one period of interest
     * and subtracts the payment. The date the balance reaches zero is the pay-off date.
     * Returns null if there is nothing to pay or the payment never covers the interest.
     */
    public LocalDate payoffDate(BigDecimal balance, BigDecimal annualRatePercent, Frequency frequency,
                                BigDecimal amount, LocalDate scheduleStart, LocalDate today) {
        if (balance == null || balance.signum() <= 0 || amount == null || amount.signum() <= 0) {
            return null;
        }
        double b = balance.doubleValue();
        double r = annualRatePercent.doubleValue() / 100.0 / frequency.getPaymentsPerYear();
        double pay = amount.doubleValue();

        // Skip payment dates already in the past; the balance is assumed current as of today.
        int k = 0;
        LocalDate date = scheduleStart;
        while (date.isBefore(today)) {
            k++;
            date = frequency.nthPaymentDate(scheduleStart, k);
        }

        for (int i = 0; i < MAX_PERIODS; i++) {
            b += b * r;
            b -= pay;
            if (b <= 0.005) {
                return date;
            }
            k++;
            date = frequency.nthPaymentDate(scheduleStart, k);
        }
        return null;
    }
}
