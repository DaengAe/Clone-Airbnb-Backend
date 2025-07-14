package com.clone.Airbnb.web.accommodation.controller;

import com.clone.Airbnb.web.accommodation.dto.AccommodationRegisterRequest;
import com.clone.Airbnb.domain.accommodation.entity.Accommodation;
import com.clone.Airbnb.domain.accommodation.service.AccommodationService;
import com.clone.Airbnb.domain.accommodation.status.AccommodationStatus;
import com.clone.Airbnb.domain.user.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<Accommodation> registerAccommodation(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AccommodationRegisterRequest request) {
        Accommodation registeredAccommodation = accommodationService.registerAccommodation(userDetails.getUserId(), request);
        return ResponseEntity.ok(registeredAccommodation);
    }

    @PostMapping("/{accommodationId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Accommodation> processAccommodationApproval(@PathVariable Long accommodationId, @RequestParam AccommodationStatus status) {
        Accommodation processedAccommodation = accommodationService.processAccommodationApproval(accommodationId, status);
        return ResponseEntity.ok(processedAccommodation);
    }

    @GetMapping
    public ResponseEntity<List<Accommodation>> getAllApprovedAccommodations() {
        List<Accommodation> accommodations = accommodationService.getAllApprovedAccommodations();
        return ResponseEntity.ok(accommodations);
    }
}