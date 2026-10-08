package edu.ucm.loantracker.repository;

import edu.ucm.loantracker.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    /** Loans that are not fully repaid (pass BigDecimal.ZERO). */
    List<Loan> findByRemainingBalanceGreaterThanOrderByStartDateDesc(BigDecimal amount);

    Optional<Loan> findFirstByCustomerId(Long customerId);
}
