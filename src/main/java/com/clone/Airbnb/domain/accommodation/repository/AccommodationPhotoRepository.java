package com.clone.Airbnb.domain.accommodation.repository;

import com.clone.Airbnb.domain.accommodation.entity.AccommodationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationPhotoRepository extends JpaRepository<AccommodationPhoto, Long> {
}
