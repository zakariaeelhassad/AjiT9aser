package com.example.backend.service;

import com.example.backend.dto.profile.ProfileResponse;
import com.example.backend.dto.profile.ProfileUpdateRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public interface ProfileService {

    ProfileResponse getMyProfile();

    ProfileResponse updateMyProfile(ProfileUpdateRequest request);

    Resource getImageAsResource(String kind, String filename);

    MediaType getImageMediaType(String kind, String filename);
}
