package edu.ucm.loantracker.controller;

import edu.ucm.loantracker.dto.CustomerUpdateRequest;
import edu.ucm.loantracker.dto.LoanDetailDto;
import edu.ucm.loantracker.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{customerId}/loan")
    public LoanDetailDto loan(@PathVariable Long customerId) {
        return customerService.getCustomerLoan(customerId);
    }

    @PutMapping("/{customerId}")
    public LoanDetailDto update(@PathVariable Long customerId, @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.updateCustomer(customerId, request);
    }
}
