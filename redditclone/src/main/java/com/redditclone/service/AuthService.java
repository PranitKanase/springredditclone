package com.redditclone.service;

import com.redditclone.dto.AuthenticationResponse;
import com.redditclone.dto.LoginRequest;
import com.redditclone.dto.RegisterRequest;
import com.redditclone.exceptions.SpringRedditException;
import com.redditclone.model.*;
import com.redditclone.repository.ProviderRepository;
import com.redditclone.repository.UserRepository;
import com.redditclone.repository.VerificationTokenRepository;
import com.redditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ProviderRepository providerRepository;

    @Transactional
    public  void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setCreated(Instant.now());
        user.setEnabled(false);
        System.out.println("Saving User");


        userRepository.save(user);

        System.out.println("Saved user");

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please activate your account",user.getEmail(),"Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));

    }

    @Transactional
    public  void signupProvider(RegisterRequest registerRequest) {
        Provider provider = new Provider();
        provider.setProviderName(registerRequest.getUsername());
        provider.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        provider.setEmail(registerRequest.getEmail());
        provider.setCreated(Instant.now());
        provider.setEnabled(false);

        providerRepository.save(provider);


        String token = generateVerificationToken(provider);
        System.out.println(token);
        mailService.sendMail(new NotificationEmail("Please activate your account",provider.getEmail(),"Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/provider/" + token));

    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    @Transactional(readOnly = true)
    public Provider getCurrentProvider() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return providerRepository.findByProviderName(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Provider  not found - " + principal.getUsername()));
    }

    private String generateVerificationToken(User user) {

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;

    }

    private String generateVerificationToken(Provider provider) {

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setProvider(provider);
        verificationTokenRepository.save(verificationToken);
        return token;

    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken =  verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        fetchUserandEnable(verificationToken.get());
    }

    public void verifyAccountProvider(String token) {
        Optional<VerificationToken> verificationToken =  verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        fetchProviderandEnable(verificationToken.get());
    }

    @Transactional
    private void fetchUserandEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - "+ username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    private void fetchProviderandEnable(VerificationToken verificationToken) {
        String providername = verificationToken.getProvider().getProviderName();
        Provider provider = providerRepository.findByProviderName(providername).orElseThrow(() -> new SpringRedditException("Provider not found with name - "+ providername));
        provider.setEnabled(true);
        providerRepository.save(provider);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token,loginRequest.getUsername());
    }


    public AuthenticationResponse loginProvider(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token,loginRequest.getUsername());
    }
}
