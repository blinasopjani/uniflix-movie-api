package com.movie.api.controller;

import com.movie.api.dto.*;
import com.movie.api.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Account and Profile management")
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new account and main profile")
    public AccountResponse register(@Valid @RequestBody UserRequest request) {
        return accountService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login to an account and get profiles")
    public AccountResponse login(@Valid @RequestBody LoginRequest request) {
        return accountService.login(request);
    }

    @PostMapping("/{accountId}/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new profile to an existing account")
    public UserResponse addProfile(@PathVariable Long accountId, @Valid @RequestBody ProfileRequest request) {
        return accountService.addProfile(accountId, request);
    }

    @PutMapping("/{accountId}/profiles/{profileId}")
    @Operation(summary = "Edit an existing profile")
    public UserResponse editProfile(@PathVariable Long accountId, @PathVariable Long profileId, @Valid @RequestBody ProfileRequest request) {
        return accountService.editProfile(accountId, profileId, request);
    }

    @DeleteMapping("/{accountId}/profiles/{profileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an existing profile")
    public void deleteProfile(@PathVariable Long accountId, @PathVariable Long profileId) {
        accountService.deleteProfile(accountId, profileId);
    }
}
