package com.clone.Airbnb.domain.hostrequest.service;


import com.clone.Airbnb.domain.hostrequest.status.HostRequestStatus;
import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.domain.hostrequest.entity.HostRequest;
import com.clone.Airbnb.domain.hostrequest.repository.HostRequestRepository;
import com.clone.Airbnb.domain.user.repository.UserRepository;
import com.clone.Airbnb.domain.user.enums.UserRole;
import com.clone.Airbnb.web.hostrequest.dto.HostApplyRequest;
import com.clone.Airbnb.web.hostrequest.dto.HostRequestStatusResponse;
import com.clone.Airbnb.web.hostrequest.dto.AdminHostRequestResponse; // AdminHostRequestResponse import 추가
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Collectors import 추가

@Service
@RequiredArgsConstructor
public class HostRequestService {

    private final HostRequestRepository hostRequestRepository;
    private final UserRepository userRepository;

    // 신청 정보 반환 (처리 상태, 신청 시간)
    @Transactional(readOnly = true)
    public Optional<HostRequestStatusResponse> getHostRequestStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return hostRequestRepository.findByUser(user)
                .map(request -> new HostRequestStatusResponse(request.getStatus(), request.getCreatedAt()));
    }



    // 신청 로직
    @Transactional
    public void applyForHost(Long userId, HostApplyRequest requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() == UserRole.HOST) {
            throw new IllegalStateException("User is already a host.");
        }

        // 유저 ID로 신청 목록에서 유저 조회
        Optional<HostRequest> existingRequest = hostRequestRepository.findByUser(user);

        if (existingRequest.isPresent()) { // 값이 있으면 업데이트
            HostRequest request = existingRequest.get();
            if (request.getStatus() == HostRequestStatus.PENDING || request.getStatus() == HostRequestStatus.APPROVED) {
                throw new IllegalStateException("Host request already pending or approved.");
            } else if (request.getStatus() == HostRequestStatus.REJECTED) {  // 거절 당한 후 재신청 시
                // 기존의 거절된 요청 업데이트
                request.setStatus(HostRequestStatus.PENDING);
                request.setBusinessRegistrationNumber(requestDto.getBusinessRegistrationNumber());
                request.setPhoneNumber(requestDto.getPhoneNumber());
                request.setCreatedAt(LocalDateTime.now()); // 신청 시간 갱신
                request.setProcessedAt(null); // 처리 시간 초기화
                hostRequestRepository.save(request);
            }
        } else {
            // 기존 요청이 없을 경우 새 요청 생성
            HostRequest hostRequest = new HostRequest();
            hostRequest.setUser(user);
            hostRequest.setBusinessRegistrationNumber(requestDto.getBusinessRegistrationNumber());
            hostRequest.setPhoneNumber(requestDto.getPhoneNumber());
            hostRequestRepository.save(hostRequest);
        }
    }

    // 호스트 신청 관리 (ADMIN)
    // 호스트 신청 목록 조회
    @Transactional(readOnly = true)
    public List<AdminHostRequestResponse> getAllPendingHostRequests() { // 반환 타입 변경
        return hostRequestRepository.findByStatus(HostRequestStatus.PENDING).stream()
                .map(request -> {
                    User user = request.getUser(); // User 엔티티 초기화
                    return new AdminHostRequestResponse(
                            request.getId(),
                            user.getId(),
                            user.getEmail(),
                            user.getName(),
                            request.getBusinessRegistrationNumber(),
                            request.getPhoneNumber(),
                            request.getStatus(),
                            request.getCreatedAt(),
                            request.getProcessedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    // 승인 처리
    @Transactional
    public void approveHostRequest(Long hostRequestId) {
        HostRequest hostRequest = hostRequestRepository.findById(hostRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Host request not found")); // 신청이 없는 경우

        // 승인 대기중이 아닌 경우
        if (hostRequest.getStatus() != HostRequestStatus.PENDING) {
            throw new IllegalStateException("Host request is not in PENDING status.");
        }

        // 상태 업데이트 (승인 대기중 -> 승인)
        hostRequest.setStatus(HostRequestStatus.APPROVED);
        hostRequest.setProcessedAt(LocalDateTime.now());
        hostRequestRepository.save(hostRequest);

        // 사용자 role 업데이트 (USER -> HOST)
        User user = hostRequest.getUser();
        user.setRole(UserRole.HOST);
        userRepository.save(user);
    }

    // 거절 처리
    @Transactional
    public void rejectHostRequest(Long hostRequestId) {
        HostRequest hostRequest = hostRequestRepository.findById(hostRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Host request not found")); // 신청이 없는 경우

        // 승인 대기중이 아닌 경우
        if (hostRequest.getStatus() != HostRequestStatus.PENDING) {
            throw new IllegalStateException("Host request is not in PENDING status.");
        }

        // 상태 업데이트 (승인 대기중 -> 거절)
        hostRequest.setStatus(HostRequestStatus.REJECTED);
        hostRequest.setProcessedAt(LocalDateTime.now());
        hostRequestRepository.save(hostRequest);
    }
}

