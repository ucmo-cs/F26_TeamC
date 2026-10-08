package edu.ucm.loantracker.service;

import edu.ucm.loantracker.dto.LoginRequest;
import edu.ucm.loantracker.dto.LoginResponse;
import edu.ucm.loantracker.exception.UnauthorizedException;
import edu.ucm.loantracker.model.AppUser;
import edu.ucm.loantracker.repository.AppUserRepository;
import edu.ucm.loantracker.repository.LoanRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final LoanRepository loanRepository;

    public AuthService(AppUserRepository userRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    public LoginResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username().trim())
                .filter(u -> u.getPassword().equals(request.password()))
                .orElseThrow(() -> new UnauthorizedException("Username or password is incorrect"));

        Long customerId = user.getCustomer() == null ? null : user.getCustomer().getId();
        Long loanId = customerId == null ? null
                : loanRepository.findFirstByCustomerId(customerId).map(l -> l.getId()).orElse(null);

        return new LoginResponse(user.getId(), user.getUsername(), user.getRole(), customerId, loanId);
    }
}
