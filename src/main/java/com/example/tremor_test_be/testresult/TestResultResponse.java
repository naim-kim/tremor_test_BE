package com.example.tremor_test_be.testresult;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestResultResponse {
    private Long id;
    private Long userId;
    private String testType;
    private LocalDateTime performedAt;
    private Double overallScore;
    private String resultCategory;
    private String csvPath;

    public static TestResultResponse from(TestResult testResult, String csvPath) {
        return TestResultResponse.builder()
                .id(testResult.getId())
                .userId(testResult.getUserId())
                .testType(testResult.getTestType())
                .performedAt(testResult.getPerformedAt())
                .overallScore(testResult.getOverallScore())
                .resultCategory(testResult.getResultCategory())
                .csvPath(csvPath)
                .build();
    }
}


