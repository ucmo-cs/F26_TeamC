package edu.ucm.loantracker.dto;

import edu.ucm.loantracker.model.Frequency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScheduleRequest(
        @NotNull Frequency frequency,
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate startDate) {
}
