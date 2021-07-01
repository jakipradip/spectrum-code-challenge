package com.spectrum.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.spectrum.dto.OrganizationInfo;
import com.spectrum.dto.ReleaseResponse;
import com.spectrum.energy.response.EnergyResponse;
import com.spectrum.energy.response.Environment;
import com.spectrum.energy.response.License;
import com.spectrum.energy.response.Release;
import com.spectrum.energy.response.ReleaseDate;
import com.spectrum.exception.InvalidEnergyResponseException;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVFormat;

@Service
public class ReleaseServiceImpl implements ReleaseService{

	private RestTemplate restTemplate;
	private String energyEndPoint;

	public ReleaseServiceImpl(RestTemplate restTemplate, @Value("${energy.endpoint}") String energyEndPoint) {
		this.restTemplate = restTemplate;
		this.energyEndPoint = energyEndPoint;
	}
	
	@Override
	public InputStreamResource getReleaseResponseForCSV(String organizationName, Integer releaseCountGreaterThan,
			Double totalLaborHoursGreaterThan, String sortBy, String sortOrder) {
	
		ReleaseResponse releaseResponse = getReleaseResponse(organizationName, releaseCountGreaterThan, totalLaborHoursGreaterThan, sortBy, sortOrder); 
		
		
		String[] csvHeader = {"Organization", "Release Count", "Total Labor Hours", "All In Production", "Licenses", "Most Active Months"};
		
		List<List<String>> csvBody = new ArrayList<>();
		releaseResponse.getOrganizations().forEach(orgInfo ->{
			csvBody.add(Arrays.asList(orgInfo.getOrganization(), 
					String.valueOf(orgInfo.getReleaseCount()), 
					String.valueOf(orgInfo.getTotalLaborHoursInOneDecimal()), 
					String.valueOf(orgInfo.getAllInProduction()), 
					orgInfo.getLicenses().stream().reduce((a,b)-> a+","+b).get(), 
					orgInfo.getMostActiveMonths().stream().map(a->""+a).reduce((a,b)->a +","+ b).get()));
		});
		
		ByteArrayInputStream byteArrayOutputStream;
		
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader(csvHeader));) {
		
			for (List<String> record : csvBody) {
				csvPrinter.printRecord(record);
			}
		
			csvPrinter.flush();

			byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		
		return new InputStreamResource(byteArrayOutputStream);
	}

	@Override
	public ReleaseResponse getReleaseResponse(String organizationName, Integer releaseCountGreaterThan,
			Double totalLaborHoursGreaterThan, String sortBy, String sortOrder) {

		EnergyResponse energyResponse = restTemplate.getForObject(energyEndPoint, EnergyResponse.class);
		validateEnergyResponse(energyResponse);
		List<OrganizationInfo> organizationInfos = groupByOrganization(energyResponse);
		
		if(organizationName != null && !organizationName.isEmpty()) {
			organizationInfos = organizationInfos.stream()
												.filter(orgInfo -> orgInfo.getOrganization().equalsIgnoreCase(organizationName))
												.collect(Collectors.toList());
		}
		
		if(releaseCountGreaterThan != null) {
			organizationInfos = organizationInfos.stream()
					.filter(orgInfo -> orgInfo.getReleaseCount() > releaseCountGreaterThan)
					.collect(Collectors.toList());
		}
		
		if(totalLaborHoursGreaterThan != null) {
			organizationInfos = organizationInfos.stream()
					.filter(orgInfo -> orgInfo.getTotalLaborHours() > totalLaborHoursGreaterThan)
					.collect(Collectors.toList());
		}
		
		if(sortBy !=null && !sortBy.isEmpty() && (sortBy.equalsIgnoreCase("releaseCount") || sortBy.equalsIgnoreCase("totalLaborHours"))) {
			final String sOrder = sortOrder == null || sortOrder.isEmpty() || !sortOrder.equalsIgnoreCase("dsc") ? "asc" : "dsc";
			
			organizationInfos.sort((orgOne, orgTwo) ->{
				if(sortBy.equalsIgnoreCase("releaseCount")) {
					if("asc".equalsIgnoreCase(sOrder)) {
						return orgOne.getReleaseCount() - orgTwo.getReleaseCount();
					}else {
						return orgTwo.getReleaseCount() - orgOne.getReleaseCount();
					}
				} else {
					if ("asc".equalsIgnoreCase(sOrder)) {
						return Double.compare(orgOne.getTotalLaborHours(), orgTwo.getTotalLaborHours());
					} else {
						return Double.compare(orgTwo.getTotalLaborHours(), orgOne.getTotalLaborHours());
					}
				}
			});
		}
		
		ReleaseResponse releaseResponse = new ReleaseResponse();
		releaseResponse.setOrganizations(organizationInfos);
		return releaseResponse;
	}

	private List<OrganizationInfo> groupByOrganization(EnergyResponse energyResponse) {
		Map<String, OrganizationInfo> organizationInfoMap = new HashMap<>();
		Map<String, Map<Integer, Integer>> mostActiveMonthsMap = new HashMap<>();
		
		for(Release release : energyResponse.getReleases()) {
			if(release.getOrganization() != null && release.getOrganization().length() !=0) {
				if(organizationInfoMap.get(release.getOrganization()) == null) {
					
					OrganizationInfo organizationInfo = new OrganizationInfo();
					organizationInfo.setOrganization(release.getOrganization());
					organizationInfo.setReleaseCount(1);
					organizationInfo.setTotalLaborHours(release.getLaborHours());
					organizationInfo.setAllInProduction(release.getStatus() == Environment.Production);
					organizationInfo.setLicenses(new ArrayList<>());
					addLicense(release, organizationInfo);
					
					organizationInfoMap.put(release.getOrganization(), organizationInfo);
				}else {
					OrganizationInfo organizationInfo = organizationInfoMap.get(release.getOrganization());
					organizationInfo.setReleaseCount(organizationInfo.getReleaseCount() + 1);
					organizationInfo.setTotalLaborHours(organizationInfo.getTotalLaborHours() + release.getLaborHours());
					organizationInfo.setAllInProduction(organizationInfo.getAllInProduction() && release.getStatus() == Environment.Production);
					addLicense(release, organizationInfo);
					
					organizationInfoMap.put(release.getOrganization(), organizationInfo);
				}
				
				addMonth(release.getDate(), release.getOrganization(), mostActiveMonthsMap);
			}
		}
		
		mostActiveMonthsMap.forEach((organization, releaseMonthCount) -> {
			int maxCount = releaseMonthCount.values().stream()
														.max(Comparator.naturalOrder())
														.get();

			List<Integer> monthsWithMaxRelease = releaseMonthCount.entrySet().stream()
																			.filter(elm -> elm.getValue() == maxCount)
																			.map(Map.Entry::getKey)
																			.collect(Collectors.toList());

			organizationInfoMap.get(organization).setMostActiveMonths(monthsWithMaxRelease);
		});
			
		return new ArrayList<>(organizationInfoMap.values());
	}

	private void addMonth(ReleaseDate date, String organization, Map<String, Map<Integer, Integer>> mostActiveMonthsMap) {
		if(date !=null && date.getCreated() != null && date.getCreated().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
			int releaseMonth = Integer.parseInt(date.getCreated().split("-")[1]);
			if(mostActiveMonthsMap.get(organization) == null) {
				Map<Integer, Integer> monthCountMap = new HashMap<>();
				monthCountMap.put(releaseMonth, 1);
				mostActiveMonthsMap.put(organization, monthCountMap);
			}else if(mostActiveMonthsMap.get(organization).get(releaseMonth) == null){
				mostActiveMonthsMap.get(organization).put(releaseMonth, 1);
			}else {
				mostActiveMonthsMap.get(organization).put(releaseMonth, mostActiveMonthsMap.get(organization).get(releaseMonth) + 1);
			}
			
		}
	}

	private void addLicense(Release release, OrganizationInfo organizationInfo) {
		if(release.getPermissions() != null && release.getPermissions().getLicenses() != null) {
			for(License license : release.getPermissions().getLicenses()) {
				if(license != null && license.getName() != null && license.getName().length() != 0) {
					organizationInfo.getLicenses().add(license.getName());
				}
			}
		}
	}

	private void validateEnergyResponse(EnergyResponse energyResponse) {
		if (energyResponse == null || energyResponse.getReleases() == null || energyResponse.getReleases().isEmpty()) {
			throw new InvalidEnergyResponseException();
		}
	}
}
