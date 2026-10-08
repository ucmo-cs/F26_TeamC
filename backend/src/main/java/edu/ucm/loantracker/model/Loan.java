package edu.ucm.loantracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal originalAmount;

    /** Kept up to date by the bank's external payment system (out of scope for this app). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingBalance;

    /** Annual rate as a percent, e.g. 6.5 means 6.5% APR. */
    @Column(nullable = false, precision = 6, scale = 3)
    private BigDecimal interestRate;

    /** Length of the loan in months; needed by the amortization formula. */
    @Column(nullable = false)
    private Integer termMonths;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
}
