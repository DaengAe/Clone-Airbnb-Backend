package com.clone.Airbnb.domain.hostrequest.service;


import com.clone.Airbnb.domain.hostrequest.status.HostRequestStatus;
import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.domain.hostrequest.entity.HostRequest;
import com.clone.Airbnb.domain.hostrequest.repository.HostRequestRepository;
import com.clone.Airbnb.domain.user.repository.UserRepository;
import com.clone.Airbnb.web.hostrequest.dto.HostApplyRequest;
import com.clone.Airbnb.web.hostrequest.dto.HostRequestStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HostRequestService {

    private final HostRequestRepository hostRequestRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<HostRequestStatusResponse> getHostRequestStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return hostRequestRepository.findByUser(user)
                .map(request -> new HostRequestStatusResponse(request.getStatus(), request.getCreatedAt()));
    }

    @Transactional
    public void applyForHost(Long userId, HostApplyRequest requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() == com.clone.Airbnb.domain.user.enums.UserRole.HOST) {
            throw new IllegalStateException("User is already a host.");
        }

        hostRequestRepository.findByUser(user).ifPresent(request -> {
            if (request.getStatus() == HostRequestStatus.PENDING) {
                throw new IllegalStateException("Host request already pending.");
            }
        });

        HostRequest hostRequest = new HostRequest();
        hostRequest.setUser(user);
        hostRequest.setBusinessRegistrationNumber(requestDto.getBusinessRegistrationNumber());
        hostRequest.setPhoneNumber(requestDto.getPhoneNumber());
        hostRequestRepository.save(hostRequest);
    }
}

