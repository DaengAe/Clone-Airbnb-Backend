package com.clone.Airbnb.web.accommodation.controller;

import com.clone.Airbnb.domain.accommodation.entity.Accommodation;
import com.clone.Airbnb.domain.accommodation.service.AccommodationService;
import com.clone.Airbnb.domain.user.details.CustomUserDetails;
import com.clone.Airbnb.web.accommodation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    // HOST 전용 엔드포인트
    // 숙소 등록 요청
    @PostMapping("/accommodations/register")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<AccommodationStatusResponse> registerAccommodation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AccommodationRegisterRequest request) {
        Accommodation registeredAccommodation = accommodationService.registerAccommodation(userDetails.getUserId(), request);
        // Accommodation 엔티티를 AccommodationStatusResponse 응답 DTO로 변환
        AccommodationStatusResponse responseDto = new AccommodationStatusResponse(
                registeredAccommodation.getId(),
                registeredAccommodation.getName(),
                registeredAccommodation.getStatus(),
                registeredAccommodation.getCreatedAt(),
                registeredAccommodation.getProcessedAt(),
                registeredAccommodation.getRejectionReason()
        );
        return ResponseEntity.ok(responseDto);
    }

    // 요청 상태 반환
    @GetMapping("/accommodations/{accommodationId}/status")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<AccommodationStatusResponse> getAccommodationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long accommodationId) {
        Optional<AccommodationStatusResponse> statusResponse = accommodationService.getAccommodationStatus(userDetails.getUserId(), accommodationId);
        return statusResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // 숙소 요청 목록 조회
    @GetMapping("/accommodations/my-accommodations")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<List<AccommodationStatusResponse>> getAllAccommodationsByHost(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AccommodationStatusResponse> accommodations = accommodationService.getAllAccommodationsByHost(userDetails.getUserId());
        return ResponseEntity.ok(accommodations);
    }

    // 숙소 상세 정보 조회
    @GetMapping("/accommodations/{accommodationId}/details")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<AccommodationDetailsResponse> getAccommodationDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long accommodationId) {
        Optional<AccommodationDetailsResponse> detailsResponse = accommodationService.getAccommodationDetails(accommodationId);
        return detailsResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 요청 정보 수정
    @PutMapping("/accommodations/{id}")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<AccommodationStatusResponse> updateAccommodation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody AccommodationRegisterRequest request) {
        Accommodation updatedAccommodation = accommodationService.updateAccommodation(id, userDetails.getUserId(), request);
        AccommodationStatusResponse responseDto = new AccommodationStatusResponse(
                updatedAccommodation.getId(),
                updatedAccommodation.getName(),
                updatedAccommodation.getStatus(),
                updatedAccommodation.getCreatedAt(),
                updatedAccommodation.getProcessedAt(),
                updatedAccommodation.getRejectionReason()
        );
        return ResponseEntity.ok(responseDto);
    }



    // 관리자용 API 엔드포인트
    // 승인 대기 목록
    @GetMapping("/admin/accommodations/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminAccommodationResponse>> getAllPendingAccommodations() {
        List<AdminAccommodationResponse> pendingRequests = accommodationService.getAllPendingAccommodations();
        return ResponseEntity.ok(pendingRequests);
    }

    // 승인 처리
    @PostMapping("/admin/accommodations/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveAccommodation(@PathVariable Long id) {
        accommodationService.approveAccommodation(id);
        return ResponseEntity.ok().build();
    }

    // 거절 처리
    @PostMapping("/admin/accommodations/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectAccommodation(@PathVariable Long id, @RequestBody AccommodationRejectRequest request) {
        accommodationService.rejectAccommodation(id, request.getRejectionReason());
        return ResponseEntity.ok().build();
    }


}
