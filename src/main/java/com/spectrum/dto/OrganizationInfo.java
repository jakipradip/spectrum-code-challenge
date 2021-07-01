package com.spectrum.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrganizationInfo {
	private String organization;
	
	@JsonProperty("release_count")
	private Integer releaseCount;
	
	@JsonIgnore
	private Double totalLaborHours;
	
	@JsonProperty("all_in_production")
	private Boolean allInProduction;
	
	private List<String> licenses;
	
	@JsonProperty("most_active_months")
	private List<Integer> mostActiveMonths;
	
	@JsonProperty("total_labor_hours")
	public Double getTotalLaborHoursInOneDecimal() {
		BigDecimal totalHours = new BigDecimal(this.totalLaborHours).setScale(1, RoundingMode.UP);
		return totalHours.doubleValue();
	}
}
