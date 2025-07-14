package com.clone.Airbnb.web.accommodation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AccommodationRegisterRequest {
    private String name;
    private String city;
    private String district;
    private String detailAddress;
    private Integer pricePerNight;
    private Integer bedroomCount;
    private String amenities;
    private String extraInfo;
    private List<String> photoUrls;
}