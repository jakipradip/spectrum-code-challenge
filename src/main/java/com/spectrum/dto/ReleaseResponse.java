package com.spectrum.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReleaseResponse {
	private List<OrganizationInfo> organizations;
}
