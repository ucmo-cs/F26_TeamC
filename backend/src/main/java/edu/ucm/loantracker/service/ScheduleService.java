package edu.ucm.loantracker.service;

import edu.ucm.loantracker.dto.MinimumPaymentDto;
import edu.ucm.loantracker.dto.ScheduleDto;
import edu.ucm.loantracker.dto.ScheduleRequest;
import edu.ucm.loantracker.exception.BadRequestException;
import edu.ucm.loantracker.exception.NotFoundException;
import edu.ucm.loantracker.model.Frequency;
import edu.ucm.loantracker.model.Loan;
import edu.ucm.loantracker.model.PaymentSchedule;
import edu.ucm.loantracker.repository.PaymentScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    private final PaymentScheduleRepository scheduleRepository;
    private final LoanService loanService;
    private final AmortizationCalculator calculator;

    public ScheduleService(PaymentScheduleRepository scheduleRepository, LoanService loanService,
                           AmortizationCalculator calculator) {
        this.scheduleRepository = scheduleRepository;
        this.loanService = loanService;
        this.calculator = calculator;
    }

    public MinimumPaymentDto getMinimum(Long loanId, Frequency frequency) {
        return calculator.minimumFor(loanService.findLoan(loanId), frequency, LocalDate.now());
    }

    public List<ScheduleDto> getSchedules(Long loanId) {
        loanService.findLoan(loanId);
        return scheduleRepository.findByLoanIdOrderByStartDateDesc(loanId).stream().map(ScheduleDto::from).toList();
    }

    /** Creates a new schedule and replaces any active one. Rejects amounts below the minimum. */
    @Transactional
    public ScheduleDto createSchedule(Long loanId, ScheduleRequest request) {
        Loan loan = loanService.findLoan(loanId);
        LocalDate today = LocalDate.now();

        if (loan.getRemainingBalance().signum() <= 0) {
            throw new BadRequestException("This loan is already paid off");
        }
        if (loan.getCustomer().getAccountNumber() == null) {
            throw new BadRequestException("Add a bank account before scheduling automatic payments");
        }
        if (request.startDate().isBefore(today)) {
            throw new BadRequestException("Start date can't be in the past");
        }

        MinimumPaymentDto min = calculator.minimumFor(loan, request.frequency(), today);
        if (request.amount().compareTo(min.minimumPayment()) < 0) {
            throw new BadRequestException("Payment must be at least $" + min.minimumPayment().toPlainString()
                    + " for a " + request.frequency().name().toLowerCase() + " schedule");
        }

        scheduleRepository.findByLoanIdAndActiveTrue(loanId).forEach(s -> s.setActive(false));

        PaymentSchedule schedule = new PaymentSchedule();
        schedule.setLoan(loan);
        schedule.setFrequency(request.frequency());
        schedule.setAmount(request.amount());
        schedule.setStartDate(request.startDate());
        schedule.setActive(true);
        return ScheduleDto.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public void cancelSchedule(Long loanId, Long scheduleId) {
        PaymentSchedule schedule = scheduleRepository.findById(scheduleId)
                .filter(s -> s.getLoan().getId().equals(loanId))
                .orElseThrow(() -> new NotFoundException("Schedule " + scheduleId + " not found on loan " + loanId));
        schedule.setActive(false);
    }
}
