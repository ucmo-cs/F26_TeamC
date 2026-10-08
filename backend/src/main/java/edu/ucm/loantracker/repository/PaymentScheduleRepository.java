package edu.ucm.loantracker.repository;

import edu.ucm.loantracker.model.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {
    List<PaymentSchedule> findByLoanIdOrderByStartDateDesc(Long loanId);
    List<PaymentSchedule> findByLoanIdAndActiveTrue(Long loanId);
}
