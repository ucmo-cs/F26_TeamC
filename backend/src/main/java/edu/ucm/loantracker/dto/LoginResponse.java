package edu.ucm.loantracker.dto;

import edu.ucm.loantracker.model.Role;

/** customerId and loanId are null for admins. */
public record LoginResponse(Long userId, String username, Role role, Long customerId, Long loanId) {
}
