package com.clone.Airbnb.web.accommodation.dto;

import com.clone.Airbnb.domain.accommodation.status.AccommodationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AdminAccommodationResponse {
    private Long id;
    private Long hostId;
    private String hostEmail;
    private String hostName;
    private String name;
    private String city;
    private String district;
    private String detailAddress;
    private Integer pricePerNight;
    private Integer bedroomCount;
    private String amenities;
    private String extraInfo;
    private AccommodationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String rejectionReason; // 거절 이유 필드 추가
}
