package com.clone.Airbnb.web.accommodation.dto;

import com.clone.Airbnb.domain.accommodation.status.AccommodationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AccommodationStatusResponse {
    private Long id;
    private String name;
    private AccommodationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String rejectionReason; // 거절 이유 필드 추가
}
