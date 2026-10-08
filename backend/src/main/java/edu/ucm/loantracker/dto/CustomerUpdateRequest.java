package edu.ucm.loantracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String phone,
        String bankName,
        String routingNumber,
        String accountNumber) {
}
