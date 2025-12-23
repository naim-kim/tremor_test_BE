package com.example.tremor_test_be.testresult;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestResultRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "testType is required")
    private String testType;

    private LocalDateTime performedAt;
    private Double overallScore;
    private String resultCategory;
    private Double frequency;
    private Double amplitude;
    private Double deviationFromBaseline;
    private Double testDuration;
    private Double averageSpeed;
    private Double mean;
    private Double std;

    /**
     * Optional raw CSV content generated on the client (includes x,y,time series).
     * When provided, the backend will write this content directly to the CSV file.
     */
    private String csvContent;
}


