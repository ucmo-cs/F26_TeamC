package edu.ucm.loantracker.controller;

import edu.ucm.loantracker.dto.MinimumPaymentDto;
import edu.ucm.loantracker.dto.ScheduleDto;
import edu.ucm.loantracker.dto.ScheduleRequest;
import edu.ucm.loantracker.model.Frequency;
import edu.ucm.loantracker.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans/{loanId}")
public class LoanController {

    private final ScheduleService scheduleService;

    public LoanController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /** e.g. GET /api/loans/1/minimum-payment?frequency=BIWEEKLY */
    @GetMapping("/minimum-payment")
    public MinimumPaymentDto minimum(@PathVariable Long loanId, @RequestParam Frequency frequency) {
        return scheduleService.getMinimum(loanId, frequency);
    }

    @GetMapping("/schedules")
    public List<ScheduleDto> schedules(@PathVariable Long loanId) {
        return scheduleService.getSchedules(loanId);
    }

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleDto> create(@PathVariable Long loanId, @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.createSchedule(loanId, request));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> cancel(@PathVariable Long loanId, @PathVariable Long scheduleId) {
        scheduleService.cancelSchedule(loanId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
