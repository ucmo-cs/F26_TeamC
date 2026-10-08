package edu.ucm.loantracker.service;

import edu.ucm.loantracker.dto.CustomerUpdateRequest;
import edu.ucm.loantracker.dto.LoanDetailDto;
import edu.ucm.loantracker.exception.NotFoundException;
import edu.ucm.loantracker.model.Customer;
import edu.ucm.loantracker.model.Loan;
import edu.ucm.loantracker.repository.CustomerRepository;
import edu.ucm.loantracker.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;

    public CustomerService(CustomerRepository customerRepository, LoanRepository loanRepository, LoanService loanService) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.loanService = loanService;
    }

    public LoanDetailDto getCustomerLoan(Long customerId) {
        return loanService.toDetail(findLoanForCustomer(customerId));
    }

    @Transactional
    public LoanDetailDto updateCustomer(Long customerId, CustomerUpdateRequest request) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " not found"));
        c.setName(request.name().trim());
        c.setEmail(request.email().trim());
        c.setPhone(request.phone().trim());
        c.setBankName(blankToNull(request.bankName()));
        c.setRoutingNumber(blankToNull(request.routingNumber()));
        c.setAccountNumber(blankToNull(request.accountNumber()));
        customerRepository.save(c);
        return loanService.toDetail(findLoanForCustomer(customerId));
    }

    private Loan findLoanForCustomer(Long customerId) {
        return loanRepository.findFirstByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("No loan found for customer " + customerId));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
