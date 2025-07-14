package com.clone.Airbnb.web.hostrequest.controller;

import com.clone.Airbnb.domain.hostrequest.service.HostRequestService;
import com.clone.Airbnb.domain.user.details.CustomUserDetails;
import com.clone.Airbnb.web.hostrequest.dto.HostApplyRequest;
import com.clone.Airbnb.web.hostrequest.dto.HostRequestStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/host-requests")
@RequiredArgsConstructor
public class HostRequestController {

    private final HostRequestService hostRequestService;

    @GetMapping("/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HostRequestStatusResponse> getHostRequestStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<HostRequestStatusResponse> statusResponse = hostRequestService.getHostRequestStatus(userDetails.getUserId());
        return statusResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> applyForHost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody HostApplyRequest requestDto) {
        hostRequestService.applyForHost(userDetails.getUserId(), requestDto);
        return ResponseEntity.ok().build();
    }
}