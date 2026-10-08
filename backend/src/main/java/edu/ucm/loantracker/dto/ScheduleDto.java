package edu.ucm.loantracker.dto;

import edu.ucm.loantracker.model.Frequency;
import edu.ucm.loantracker.model.PaymentSchedule;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScheduleDto(Long id, Frequency frequency, BigDecimal amount, LocalDate startDate, boolean active) {
    public static ScheduleDto from(PaymentSchedule s) {
        return new ScheduleDto(s.getId(), s.getFrequency(), s.getAmount(), s.getStartDate(), s.isActive());
    }
}
