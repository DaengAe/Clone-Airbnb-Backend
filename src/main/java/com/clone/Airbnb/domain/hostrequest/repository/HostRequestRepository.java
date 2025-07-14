package com.clone.Airbnb.domain.hostrequest.repository;

import com.clone.Airbnb.domain.hostrequest.status.HostRequestStatus;
import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.domain.hostrequest.entity.HostRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HostRequestRepository extends JpaRepository<HostRequest, Long> {
    Optional<HostRequest> findByUser(User user);

    List<HostRequest> findByStatus(HostRequestStatus status);
}
