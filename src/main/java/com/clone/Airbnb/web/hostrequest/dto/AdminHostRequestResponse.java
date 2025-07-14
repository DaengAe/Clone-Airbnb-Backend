package com.clone.Airbnb.web.hostrequest.dto;

import com.clone.Airbnb.domain.hostrequest.status.HostRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AdminHostRequestResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private String businessRegistrationNumber;
    private String phoneNumber;
    private HostRequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
