package com.clone.Airbnb.domain.accommodation.service;

import com.clone.Airbnb.web.accommodation.dto.AccommodationRegisterRequest;
import com.clone.Airbnb.domain.accommodation.entity.Accommodation;
import com.clone.Airbnb.domain.accommodation.entity.AccommodationPhoto;
import com.clone.Airbnb.domain.accommodation.repository.AccommodationPhotoRepository;
import com.clone.Airbnb.domain.accommodation.repository.AccommodationRepository;
import com.clone.Airbnb.domain.accommodation.status.AccommodationStatus;
import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.domain.user.repository.UserRepository;
import com.clone.Airbnb.web.accommodation.dto.AccommodationRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationPhotoRepository accommodationPhotoRepository;
    private final UserRepository userRepository;

    @Transactional
    public Accommodation registerAccommodation(Long hostId, AccommodationRegisterRequest request) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Host not found"));

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
        accommodation.setStatus(AccommodationStatus.PENDING); // 초기 상태 PENDING

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

    @Transactional
    public Accommodation processAccommodationApproval(Long accommodationId, AccommodationStatus status) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        if (accommodation.getStatus() != AccommodationStatus.PENDING) {
            throw new IllegalStateException("Accommodation is not in PENDING status.");
        }

        accommodation.setStatus(status);
        return accommodationRepository.save(accommodation);
    }

    @Transactional(readOnly = true)
    public List<Accommodation> getAllApprovedAccommodations() {
        return accommodationRepository.findByStatus(AccommodationStatus.APPROVED);
    }
}
