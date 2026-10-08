package edu.ucm.loantracker.dto;

/** Returns the new login too, so the admin can share it if the email couldn't be sent. */
public record CreateLoanResponse(LoanDetailDto loan, String username, String temporaryPassword, boolean emailSent) {
}
