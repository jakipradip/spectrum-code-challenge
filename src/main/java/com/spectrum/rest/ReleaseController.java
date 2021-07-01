package com.spectrum.rest;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spectrum.dto.ReleaseResponse;
import com.spectrum.service.ReleaseService;

@RestController
public class ReleaseController {
	private ReleaseService releaseService;
	
	public ReleaseController(ReleaseService releaseService) {
		this.releaseService = releaseService;
	}

	@GetMapping("/release/info")
	public ReleaseResponse getReleaseResponse(@RequestParam(required = false) String organization, @RequestParam(required = false) Integer releaseCountGreaterThan, 
			@RequestParam(required = false) Double totalLaborHoursGreaterThan, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortOrder) {
		
		return releaseService.getReleaseResponse(organization,releaseCountGreaterThan,totalLaborHoursGreaterThan,sortBy,sortOrder);
	}
	
	@GetMapping(value="/release/info/exportCsv", produces = "text/csv")
	public ResponseEntity<Resource> getReleaseResponseInCSV(@RequestParam(required = false) String organization, @RequestParam(required = false) Integer releaseCountGreaterThan, 
			@RequestParam(required = false) Double totalLaborHoursGreaterThan, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortOrder) {

	    HttpHeaders headers = new HttpHeaders();
	    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=org_release_info.csv");
	    headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");
	    
	    return new ResponseEntity<>(
	    		releaseService.getReleaseResponseForCSV(organization,releaseCountGreaterThan,totalLaborHoursGreaterThan,sortBy,sortOrder),
	            headers,
	            HttpStatus.OK
	    );
	}
	
}
