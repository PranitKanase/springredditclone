package com.redditclone.controller;

import com.redditclone.dto.AuthenticationResponse;
import com.redditclone.dto.LoginRequest;
import com.redditclone.dto.RegisterRequest;
import com.redditclone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity<>(
                "User Registration successful", HttpStatus.OK);
    }

    @PostMapping("/signup-provider")
    public ResponseEntity<String> signupProvider(@RequestBody RegisterRequest registerRequest) {
        authService.signupProvider(registerRequest);
        return new ResponseEntity<>(
                "Provider Registration successful", HttpStatus.OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }

    @GetMapping("accountVerification/provider/{token}")
    public ResponseEntity<String> verifyAccountProvider(@PathVariable String token) {
        authService.verifyAccountProvider(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/login/provider")
    public AuthenticationResponse loginProvider(@RequestBody LoginRequest loginRequest) {
        return authService.loginProvider(loginRequest);
    }
}
