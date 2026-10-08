package edu.ucm.loantracker.controller;

import edu.ucm.loantracker.dto.LoginRequest;
import edu.ucm.loantracker.dto.LoginResponse;
import edu.ucm.loantracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
