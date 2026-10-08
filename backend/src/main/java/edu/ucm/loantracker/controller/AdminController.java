package edu.ucm.loantracker.controller;

import edu.ucm.loantracker.dto.CreateLoanRequest;
import edu.ucm.loantracker.dto.CreateLoanResponse;
import edu.ucm.loantracker.dto.LoanDetailDto;
import edu.ucm.loantracker.dto.LoanSummaryDto;
import edu.ucm.loantracker.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final LoanService loanService;

    public AdminController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/loans")
    public List<LoanSummaryDto> openLoans() {
        return loanService.getOpenLoans();
    }

    @GetMapping("/loans/{id}")
    public LoanDetailDto loan(@PathVariable Long id) {
        return loanService.getLoanDetail(id);
    }

    @PostMapping("/loans")
    public ResponseEntity<CreateLoanResponse> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(request));
    }
}
