package edu.ucm.loantracker.dto;

import edu.ucm.loantracker.model.Frequency;

import java.math.BigDecimal;

public record MinimumPaymentDto(Frequency frequency, int paymentsPerYear, int paymentsRemaining, BigDecimal minimumPayment) {
}
