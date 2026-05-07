package com.movie.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String email;
    private List<UserResponse> profiles;
}
