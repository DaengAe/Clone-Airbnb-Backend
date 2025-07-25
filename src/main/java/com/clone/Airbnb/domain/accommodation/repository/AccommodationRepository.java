package com.clone.Airbnb.domain.accommodation.repository;

import com.clone.Airbnb.domain.accommodation.entity.Accommodation;
import com.clone.Airbnb.domain.accommodation.status.AccommodationStatus;
import com.clone.Airbnb.domain.user.entity.User; // User import 추가
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findByStatus(AccommodationStatus status);
    List<Accommodation> findByHost(User host); // findByHost 메서드 추가
}
