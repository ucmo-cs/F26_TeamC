package edu.ucm.loantracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** A recurring automatic payment. Only one schedule per loan is active at a time. */
@Entity
@Table(name = "payment_schedules")
public class PaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** First payment date. Monthly repeats on this day of month; weekly/bi-weekly on this weekday. */
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }
    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
