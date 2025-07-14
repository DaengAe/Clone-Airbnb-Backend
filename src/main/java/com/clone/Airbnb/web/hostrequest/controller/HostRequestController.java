package com.clone.Airbnb.web.hostrequest.controller;

import com.clone.Airbnb.domain.hostrequest.service.HostRequestService;
import com.clone.Airbnb.domain.user.details.CustomUserDetails;
import com.clone.Airbnb.web.hostrequest.dto.HostApplyRequest;
import com.clone.Airbnb.web.hostrequest.dto.HostRequestStatusResponse;
import com.clone.Airbnb.web.hostrequest.dto.AdminHostRequestResponse;
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
public class HostRequestController {

    private final HostRequestService hostRequestService;

    // 신청 상태 반환
    @GetMapping("/host-requests/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HostRequestStatusResponse> getHostRequestStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<HostRequestStatusResponse> statusResponse = hostRequestService.getHostRequestStatus(userDetails.getUserId());
        return statusResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // 신청 로직
    @PostMapping("/host-requests/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> applyForHost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody HostApplyRequest requestDto) {
        hostRequestService.applyForHost(userDetails.getUserId(), requestDto);
        return ResponseEntity.ok().build();
    }





    // 관리자용 API 엔드포인트
    // 승인 대기 목록
    @GetMapping("/admin/host-requests/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminHostRequestResponse>> getAllPendingHostRequests() { // 반환 타입 변경
        List<AdminHostRequestResponse> pendingRequests = hostRequestService.getAllPendingHostRequests();
        return ResponseEntity.ok(pendingRequests);
    }

    // 승인 처리
    @PostMapping("/admin/host-requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveHostRequest(@PathVariable Long id) {
        hostRequestService.approveHostRequest(id);
        return ResponseEntity.ok().build();
    }

    // 거절 처리
    @PostMapping("/admin/host-requests/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectHostRequest(@PathVariable Long id) {
        hostRequestService.rejectHostRequest(id);
        return ResponseEntity.ok().build();
    }
}