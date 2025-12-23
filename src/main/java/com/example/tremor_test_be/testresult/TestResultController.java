package com.example.tremor_test_be.testresult;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestResultController {

    private final TestResultService testResultService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestResultResponse> saveTestResult(
            @RequestParam("metadata") String metadataJson,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        try {
            // Parse JSON metadata
            Map<String, Object> metadata = objectMapper.readValue(metadataJson, Map.class);
            
            TestResultRequest request = new TestResultRequest();
            request.setUserId(((Number) metadata.get("userId")).longValue());
            request.setTestType((String) metadata.get("testType"));
            
            if (metadata.containsKey("performedAt") && metadata.get("performedAt") != null) {
                request.setPerformedAt(java.time.LocalDateTime.parse((String) metadata.get("performedAt")));
            }
            if (metadata.containsKey("overallScore") && metadata.get("overallScore") != null) {
                request.setOverallScore(((Number) metadata.get("overallScore")).doubleValue());
            }
            if (metadata.containsKey("resultCategory") && metadata.get("resultCategory") != null) {
                request.setResultCategory((String) metadata.get("resultCategory"));
            }
            if (metadata.containsKey("frequency") && metadata.get("frequency") != null) {
                request.setFrequency(((Number) metadata.get("frequency")).doubleValue());
            }
            if (metadata.containsKey("amplitude") && metadata.get("amplitude") != null) {
                request.setAmplitude(((Number) metadata.get("amplitude")).doubleValue());
            }
            if (metadata.containsKey("deviationFromBaseline") && metadata.get("deviationFromBaseline") != null) {
                request.setDeviationFromBaseline(((Number) metadata.get("deviationFromBaseline")).doubleValue());
            }
            if (metadata.containsKey("testDuration") && metadata.get("testDuration") != null) {
                request.setTestDuration(((Number) metadata.get("testDuration")).doubleValue());
            }
            if (metadata.containsKey("averageSpeed") && metadata.get("averageSpeed") != null) {
                request.setAverageSpeed(((Number) metadata.get("averageSpeed")).doubleValue());
            }
            if (metadata.containsKey("mean") && metadata.get("mean") != null) {
                request.setMean(((Number) metadata.get("mean")).doubleValue());
            }
            if (metadata.containsKey("std") && metadata.get("std") != null) {
                request.setStd(((Number) metadata.get("std")).doubleValue());
            }
            if (metadata.containsKey("csvContent") && metadata.get("csvContent") != null) {
                request.setCsvContent((String) metadata.get("csvContent"));
            }
            
            TestResultResponse response = testResultService.saveResultAndExport(request, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Failed to parse metadata: " + e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<TestResultResponse>> getResultsForUser(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "testType", required = false) String testType) {
        List<TestResultResponse> responses = testResultService.getResultsForUser(userId, testType);
        return ResponseEntity.ok(responses);
    }
}


