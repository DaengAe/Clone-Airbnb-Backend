package com.clone.Airbnb.domain.accommodation.entity;

import com.clone.Airbnb.domain.accommodation.status.AccommodationStatus;
import com.clone.Airbnb.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accommodations")
@Getter @Setter
@NoArgsConstructor
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String district;

    @Column(name = "detail_address", nullable = false, length = 255)
    private String detailAddress;

    @Column(name = "price_per_night", nullable = false)
    private Integer pricePerNight;

    @Column(name = "bedroom_count", nullable = false)
    private Integer bedroomCount;

    @Column(length = 255)
    private String amenities;

    @Column(name = "extra_info", columnDefinition = "TEXT")
    private String extraInfo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at") // processed_at 필드 추가
    private LocalDateTime processedAt;

    @Column(name = "rejection_reason", length = 500) // 거절 이유 필드 추가
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationStatus status;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccommodationPhoto> photos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = AccommodationStatus.PENDING;
    }
}
