package edu.ucm.loantracker.service;

import edu.ucm.loantracker.dto.*;
import edu.ucm.loantracker.exception.NotFoundException;
import edu.ucm.loantracker.model.*;
import edu.ucm.loantracker.repository.AppUserRepository;
import edu.ucm.loantracker.repository.CustomerRepository;
import edu.ucm.loantracker.repository.LoanRepository;
import edu.ucm.loantracker.repository.PaymentScheduleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private final SecureRandom random = new SecureRandom();

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final AppUserRepository userRepository;
    private final PaymentScheduleRepository scheduleRepository;
    private final AmortizationCalculator calculator;
    private final EmailService emailService;
    private final int defaultTermMonths;

    public LoanService(LoanRepository loanRepository,
                       CustomerRepository customerRepository,
                       AppUserRepository userRepository,
                       PaymentScheduleRepository scheduleRepository,
                       AmortizationCalculator calculator,
                       EmailService emailService,
                       @Value("${app.loan.default-term-months:120}") int defaultTermMonths) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.calculator = calculator;
        this.emailService = emailService;
        this.defaultTermMonths = defaultTermMonths;
    }

    /** Admin table: every loan that isn't fully repaid. */
    public List<LoanSummaryDto> getOpenLoans() {
        return loanRepository.findByRemainingBalanceGreaterThanOrderByStartDateDesc(BigDecimal.ZERO).stream()
                .map(l -> new LoanSummaryDto(l.getId(), l.getCustomer().getName(), l.getStartDate(),
                        l.getRemainingBalance(), l.getOriginalAmount(), l.getInterestRate()))
                .toList();
    }

    public LoanDetailDto getLoanDetail(Long loanId) {
        return toDetail(findLoan(loanId));
    }

    public Loan findLoan(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan " + loanId + " not found"));
    }

    /** Admin creates a loan: also creates the customer, their login, and emails them. */
    @Transactional
    public CreateLoanResponse createLoan(CreateLoanRequest request) {
        Customer customer = new Customer();
        customer.setName(request.customerName().trim());
        customer.setEmail(request.email().trim());
        customer.setPhone(request.phone().trim());
        customerRepository.save(customer);

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setStartDate(request.startDate());
        loan.setOriginalAmount(request.originalAmount());
        loan.setRemainingBalance(request.originalAmount());
        loan.setInterestRate(request.interestRate());
        loan.setTermMonths(request.termMonths() != null ? request.termMonths() : defaultTermMonths);
        loanRepository.save(loan);

        String username = generateUsername(customer.getEmail());
        String password = generatePassword();
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.CUSTOMER);
        user.setCustomer(customer);
        userRepository.save(user);

        boolean sent = emailService.sendAccountEmail(customer.getEmail(), customer.getName(), username, password);
        return new CreateLoanResponse(toDetail(loan), username, password, sent);
    }

    /** Builds the full loan view, including the calculated pay-off date and minimum monthly payment. */
    public LoanDetailDto toDetail(Loan loan) {
        Customer c = loan.getCustomer();
        LocalDate today = LocalDate.now();

        List<PaymentSchedule> schedules = scheduleRepository.findByLoanIdOrderByStartDateDesc(loan.getId());
        PaymentSchedule active = schedules.stream().filter(PaymentSchedule::isActive).findFirst().orElse(null);

        LocalDate payoff = active == null ? null : calculator.payoffDate(
                loan.getRemainingBalance(), loan.getInterestRate(), active.getFrequency(),
                active.getAmount(), active.getStartDate(), today);
        BigDecimal minMonthly = calculator.minimumFor(loan, Frequency.MONTHLY, today).minimumPayment();

        return new LoanDetailDto(
                loan.getId(), c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                loan.getStartDate(), loan.getRemainingBalance(), loan.getOriginalAmount(),
                loan.getInterestRate(), loan.getTermMonths(),
                c.getBankName(), c.getRoutingNumber(), c.getAccountNumber(),
                schedules.stream().map(ScheduleDto::from).toList(),
                payoff, minMonthly);
    }

    private String generateUsername(String email) {
        String base = email.substring(0, email.indexOf('@')).toLowerCase().replaceAll("[^a-z0-9]", "");
        if (base.isBlank()) {
            base = "customer";
        }
        String candidate = base;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + (100 + random.nextInt(900));
        }
        return candidate;
    }

    private String generatePassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
