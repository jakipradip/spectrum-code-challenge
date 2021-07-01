package com.spectrum.service;

import org.springframework.core.io.InputStreamResource;

import com.spectrum.dto.ReleaseResponse;

public interface ReleaseService {

	InputStreamResource getReleaseResponseForCSV(String organizationName, Integer releaseCountGreaterThan,
			Double totalLaborHoursGreaterThan, String sortBy, String sortOrder);

	ReleaseResponse getReleaseResponse(String organizationName, Integer releaseCountGreaterThan,
			Double totalLaborHoursGreaterThan, String sortBy, String sortOrder);

}
