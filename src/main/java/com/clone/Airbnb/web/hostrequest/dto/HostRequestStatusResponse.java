package com.clone.Airbnb.web.hostrequest.dto;

import com.clone.Airbnb.domain.hostrequest.status.HostRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HostRequestStatusResponse {
    private HostRequestStatus status;
    private LocalDateTime requestDate;
}