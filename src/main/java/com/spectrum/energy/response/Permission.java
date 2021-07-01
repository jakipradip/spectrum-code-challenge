package com.spectrum.energy.response;

import java.util.List;

import lombok.Data;

@Data
public class Permission {
	private List<License> licenses;
}
