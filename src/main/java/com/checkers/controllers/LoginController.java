package com.checkers.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository securityContextRepository;

    public LoginController(AuthenticationManager authenticationManager,
            HttpSessionSecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        System.out.println("in LoginController");
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);

        // Save the authenticated user in the SecurityContextRepository
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        this.securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, null);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userName", userDetails.getUsername());
        userInfo.put("authorities", userDetails.getAuthorities());
        return ResponseEntity.status(HttpStatus.OK).body(userInfo);
    }
    @GetMapping("/auth")
    public ResponseEntity<?> auth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Retrieve user details from authentication object if needed
           UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userName", userDetails.getUsername());
            userInfo.put("authorities", userDetails.getAuthorities());
            return ResponseEntity.ok(userInfo);
        } else {
            return ResponseEntity.status(HttpStatus.SEE_OTHER).build();
        }
    }


    public record LoginRequest(String username, String password) {
    }

}