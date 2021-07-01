package com.spectrum.energy.response;

import lombok.Data;

@Data
public class Release {
	private ReleaseDate date;
	private double laborHours;
	private String name;
	private String organization;
	private Permission permissions;
	private Environment status;
}
