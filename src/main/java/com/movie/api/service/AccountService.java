package com.movie.api.service;

import com.movie.api.dto.*;
import com.movie.api.entity.Account;
import com.movie.api.entity.User;
import com.movie.api.exception.ConflictException;
import com.movie.api.exception.ResourceNotFoundException;
import com.movie.api.exception.UnauthorizedException;
import com.movie.api.repository.AccountRepository;
import com.movie.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse register(UserRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        Account account = Account.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        account = accountRepository.save(account);

        return mapToResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email nuk ekziston!"));

        if (!account.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Fjalëkalimi është i gabuar!");
        }

        return mapToResponse(account);
    }

    @Transactional
    public UserResponse addProfile(Long accountId, ProfileRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        User newProfile = User.builder()
                .name(request.getName())
                .avatarUrl(request.getAvatarUrl() != null ? request.getAvatarUrl() : "https://upload.wikimedia.org/wikipedia/commons/0/0b/Netflix-avatar.png")
                .account(account)
                .build();

        User savedProfile = userRepository.save(newProfile);
        
        return UserResponse.builder()
                .id(savedProfile.getId())
                .name(savedProfile.getName())
                .avatarUrl(savedProfile.getAvatarUrl())
                .build();
    }

    @Transactional
    public UserResponse editProfile(Long accountId, Long profileId, ProfileRequest request) {
        User profile = userRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", profileId));
        
        if (!profile.getAccount().getId().equals(accountId)) {
            throw new UnauthorizedException("Profili nuk i përket kësaj llogarie!");
        }

        profile.setName(request.getName());
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }

        User savedProfile = userRepository.save(profile);
        return UserResponse.builder()
                .id(savedProfile.getId())
                .name(savedProfile.getName())
                .avatarUrl(savedProfile.getAvatarUrl())
                .build();
    }

    @Transactional
    public void deleteProfile(Long accountId, Long profileId) {
        User profile = userRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", profileId));
        
        if (!profile.getAccount().getId().equals(accountId)) {
            throw new UnauthorizedException("Profili nuk i përket kësaj llogarie!");
        }

        userRepository.delete(profile);
    }

    public AccountResponse mapToResponse(Account account) {
        List<UserResponse> userResponses = new ArrayList<>();
        if (account.getProfiles() != null) {
            userResponses = account.getProfiles().stream()
                    .map(p -> UserResponse.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .avatarUrl(p.getAvatarUrl())
                            .build())
                    .collect(Collectors.toList());
        }

        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .profiles(userResponses)
                .build();
    }
}
