package com.example.tremor_test_be.testresult;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test_results")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String testType;

    @Column(nullable = false)
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

    @Column(name = "csv_path", length = 500)
    private String csvPath;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @PrePersist
    void onCreate() {
        if (performedAt == null) {
            performedAt = LocalDateTime.now();
        }
    }
}


