package com.clone.Airbnb.domain.accommodation.service;

import com.clone.Airbnb.domain.accommodation.entity.Accommodation;
import com.clone.Airbnb.domain.accommodation.entity.AccommodationPhoto;
import com.clone.Airbnb.domain.accommodation.repository.AccommodationPhotoRepository;
import com.clone.Airbnb.domain.accommodation.repository.AccommodationRepository;
import com.clone.Airbnb.domain.accommodation.status.AccommodationStatus;
import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.domain.user.repository.UserRepository;
import com.clone.Airbnb.web.accommodation.dto.AccommodationRegisterRequest;
import com.clone.Airbnb.web.accommodation.dto.AccommodationStatusResponse; // Import AccommodationStatusResponse
import com.clone.Airbnb.web.accommodation.dto.AdminAccommodationResponse; // Import AdminAccommodationResponse
import com.clone.Airbnb.web.accommodation.dto.AccommodationDetailsResponse; // Import AccommodationDetailsResponse
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationPhotoRepository accommodationPhotoRepository;
    private final UserRepository userRepository;

    // 숙소 등록 로직
    @Transactional
    public Accommodation registerAccommodation(Long hostId, AccommodationRegisterRequest request) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("호스트를 찾을 수 없습니다."));

        Accommodation accommodation = getAccommodation(request, host);

        Accommodation savedAccommodation = accommodationRepository.save(accommodation);

        if (request.getPhotoUrls() != null && !request.getPhotoUrls().isEmpty()) {
            List<AccommodationPhoto> photos = request.getPhotoUrls().stream()
                    .map(url -> {
                        AccommodationPhoto photo = new AccommodationPhoto();
                        photo.setAccommodation(savedAccommodation);
                        photo.setPhotoUrl(url);
                        return photo;
                    })
                    .toList();
            accommodationPhotoRepository.saveAll(photos);
            savedAccommodation.setPhotos(photos);
        }

        return savedAccommodation;
    }
    // 등록 정보
    private static Accommodation getAccommodation(AccommodationRegisterRequest request, User host) {
        Accommodation accommodation = new Accommodation();
        accommodation.setHost(host);
        accommodation.setName(request.getName());
        accommodation.setCity(request.getCity());
        accommodation.setDistrict(request.getDistrict());
        accommodation.setDetailAddress(request.getDetailAddress());
        accommodation.setPricePerNight(request.getPricePerNight());
        accommodation.setBedroomCount(request.getBedroomCount());
        accommodation.setAmenities(request.getAmenities());
        accommodation.setExtraInfo(request.getExtraInfo());
        return accommodation;
    }

    // 상태 조회
    @Transactional(readOnly = true)
    public Optional<AccommodationStatusResponse> getAccommodationStatus(Long hostId, Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

        if (!accommodation.getHost().getId().equals(hostId)) {
            throw new IllegalArgumentException("해당 숙소에 대한 접근 권한이 없습니다.");
        }

        return Optional.of(new AccommodationStatusResponse(
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getStatus(),
                accommodation.getCreatedAt(),
                accommodation.getProcessedAt(),
                accommodation.getRejectionReason()
        ));
    }

    // 승인 대기중인 목록 조회
    @Transactional(readOnly = true)
    public List<AdminAccommodationResponse> getAllPendingAccommodations() {
        return accommodationRepository.findByStatus(AccommodationStatus.PENDING).stream()
                .map(accommodation -> {
                    User host = accommodation.getHost(); // Lazy Loading(지연 로딩) 문제를 피하기 위해 host 필드를 미리 초기화
                    return new AdminAccommodationResponse(
                            accommodation.getId(),
                            host.getId(),
                            host.getEmail(),
                            host.getName(),
                            accommodation.getName(),
                            accommodation.getCity(),
                            accommodation.getDistrict(),
                            accommodation.getDetailAddress(),
                            accommodation.getPricePerNight(),
                            accommodation.getBedroomCount(),
                            accommodation.getAmenities(),
                            accommodation.getExtraInfo(),
                            accommodation.getStatus(),
                            accommodation.getCreatedAt(),
                            accommodation.getProcessedAt(),
                            accommodation.getRejectionReason()
                    );
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void approveAccommodation(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

        if (accommodation.getStatus() != AccommodationStatus.PENDING) {
            throw new IllegalStateException("해당 숙소는 [승인 대기중]이 아닙니다.");
        }

        accommodation.setStatus(AccommodationStatus.APPROVED);
        accommodation.setProcessedAt(LocalDateTime.now());
        accommodationRepository.save(accommodation);
    }

    @Transactional
    public void rejectAccommodation(Long accommodationId, String rejectionReason) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

        if (accommodation.getStatus() != AccommodationStatus.PENDING) {
            throw new IllegalStateException("해당 숙소는 [승인 대기중]이 아닙니다.");
        }

        accommodation.setStatus(AccommodationStatus.REJECTED);
        accommodation.setProcessedAt(LocalDateTime.now());
        accommodation.setRejectionReason(rejectionReason); // 거절 사유 설정
        accommodationRepository.save(accommodation);
    }

    @Transactional(readOnly = true)
    public Optional<AccommodationDetailsResponse> getAccommodationDetails(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

        return Optional.of(new AccommodationDetailsResponse(
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getCity(),
                accommodation.getDistrict(),
                accommodation.getDetailAddress(),
                accommodation.getPricePerNight(),
                accommodation.getBedroomCount(),
                accommodation.getAmenities(),
                accommodation.getExtraInfo(),
                accommodation.getPhotos().stream().map(AccommodationPhoto::getPhotoUrl).collect(Collectors.toList()) // 사진 URL들
        ));
    }

    @Transactional(readOnly = true)
    public List<AccommodationStatusResponse> getAllAccommodationsByHost(Long hostId) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("호스트를 찾을 수 없습니다."));

        return accommodationRepository.findByHost(host).stream()
                .map(accommodation -> new AccommodationStatusResponse(
                        accommodation.getId(),
                        accommodation.getName(),
                        accommodation.getStatus(),
                        accommodation.getCreatedAt(),
                        accommodation.getProcessedAt(),
                        accommodation.getRejectionReason()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Accommodation> getAllApprovedAccommodations() {
        return accommodationRepository.findByStatus(AccommodationStatus.APPROVED);
    }

    @Transactional
    public Accommodation updateAccommodation(Long accommodationId, Long hostId, AccommodationRegisterRequest request) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

        if (!accommodation.getHost().getId().equals(hostId)) {
            throw new IllegalArgumentException("해당 숙소에 대한 접근 권한이 없습니다.");
        }

        // 필드 업데이트
        accommodation.setName(request.getName());
        accommodation.setCity(request.getCity());
        accommodation.setDistrict(request.getDistrict());
        accommodation.setDetailAddress(request.getDetailAddress());
        accommodation.setPricePerNight(request.getPricePerNight());
        accommodation.setBedroomCount(request.getBedroomCount());
        accommodation.setAmenities(request.getAmenities());
        accommodation.setExtraInfo(request.getExtraInfo());
        accommodation.setStatus(AccommodationStatus.PENDING); // 상태 업데이트
        accommodation.setProcessedAt(null); // 승인 시간 초기화
        accommodation.setRejectionReason(null); // 거절 사유 초기화

        // 사진 업데이트: 기존 사진을 삭제하고 새로운 사진을 관리되는 컬렉션에 추가
        accommodation.getPhotos().clear(); // 기존 사진들이 고아 객체 제거(Orphan Removal) 트리거로 삭제됨.

        if (request.getPhotoUrls() != null && !request.getPhotoUrls().isEmpty()) {
            for (String url : request.getPhotoUrls()) {
                AccommodationPhoto photo = new AccommodationPhoto();
                photo.setAccommodation(accommodation); // 부모 참조 설정
                photo.setPhotoUrl(url);
                accommodation.getPhotos().add(photo); // 컬렉션에 사진 추가
            }
        }

        return accommodationRepository.save(accommodation);
    }
}
