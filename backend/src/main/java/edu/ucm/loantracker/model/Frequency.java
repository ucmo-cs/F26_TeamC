package edu.ucm.loantracker.model;

import java.time.LocalDate;

/** How often an automatic payment repeats, and how many payments that makes per year. */
public enum Frequency {
    MONTHLY(12),   // same date every month
    BIWEEKLY(26),  // same weekday every two weeks
    WEEKLY(52);    // same weekday every week

    private final int paymentsPerYear;

    Frequency(int paymentsPerYear) {
        this.paymentsPerYear = paymentsPerYear;
    }

    public int getPaymentsPerYear() {
        return paymentsPerYear;
    }

    /** Date of payment number n (0 = the first payment on the start date). */
    public LocalDate nthPaymentDate(LocalDate start, int n) {
        return switch (this) {
            case MONTHLY -> start.plusMonths(n);
            case BIWEEKLY -> start.plusWeeks(2L * n);
            case WEEKLY -> start.plusWeeks(n);
        };
    }
}
