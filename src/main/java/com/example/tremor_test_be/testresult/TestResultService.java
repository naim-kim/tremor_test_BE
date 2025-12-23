package com.example.tremor_test_be.testresult;

import com.example.tremor_test_be.user.UserRepository;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TestResultService {

    private static final DateTimeFormatter FILE_TS =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;

    @Value("${file.export.base-dir:..}")
    private String exportBaseDir;

    @Value("${file.export.dir-name:tremor_test_exports}")
    private String exportDirName;

    @Transactional(readOnly = true)
    public java.util.List<TestResultResponse> getResultsForUser(Long userId, String testType) {
        java.util.List<TestResult> results;
        if (testType != null && !testType.isBlank()) {
            results = testResultRepository.findByUserIdAndTestTypeOrderByPerformedAtDesc(userId, testType);
        } else {
            results = testResultRepository.findByUserIdOrderByPerformedAtDesc(userId);
        }
        return results.stream()
                .map(r -> TestResultResponse.from(r, r.getCsvPath()))
                .toList();
    }

    @Transactional
    public TestResultResponse saveResultAndExport(@Valid TestResultRequest request, org.springframework.web.multipart.MultipartFile imageFile) {
        // Basic guard that the user exists to avoid orphan data.
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found for id=" + request.getUserId()));

        TestResult toSave = TestResult.builder()
                .userId(request.getUserId())
                .testType(request.getTestType())
                .performedAt(request.getPerformedAt() != null
                        ? request.getPerformedAt()
                        : LocalDateTime.now())
                .overallScore(request.getOverallScore())
                .resultCategory(request.getResultCategory())
                .frequency(request.getFrequency())
                .amplitude(request.getAmplitude())
                .deviationFromBaseline(request.getDeviationFromBaseline())
                .testDuration(request.getTestDuration())
                .averageSpeed(request.getAverageSpeed())
                .mean(request.getMean())
                .std(request.getStd())
                .build();

        TestResult saved = testResultRepository.save(toSave);
        String csvPath = writeCsv(saved, request.getCsvContent());
        saved.setCsvPath(csvPath);
        
        // Save PNG image if provided (for pentagon tests)
        if (imageFile != null && !imageFile.isEmpty() && "pentagon".equalsIgnoreCase(request.getTestType())) {
            String imagePath = writePng(saved, imageFile);
            saved.setImagePath(imagePath);
        }
        
        testResultRepository.save(saved);
        return TestResultResponse.from(saved, csvPath);
    }

    private String writeCsv(TestResult result, String csvContent) {
        Path basePath = resolveBasePath();
        Path exportDir = basePath.resolve(exportDirName);
        try {
            Files.createDirectories(exportDir);
            String testType = result.getTestType() != null ? result.getTestType() : "unknown";
            String fileName = String.format(
                    "%s_%s_%d.csv",
                    testType,
                    FILE_TS.format(result.getPerformedAt()),
                    result.getId());
            Path csvFile = exportDir.resolve(fileName);

            if (Files.exists(csvFile)) {
                // extremely unlikely with timestamp + userId, but avoid overwrite
                csvFile = exportDir.resolve(fileName.replace(".csv", "") + "_" + UUID.randomUUID() + ".csv");
            }

            if (csvContent != null && !csvContent.isBlank()) {
                Files.writeString(
                        csvFile, csvContent, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            } else {
                List<String> lines = List.of(
                        String.join(",",
                                "id",
                                "userId",
                                "testType",
                                "performedAt",
                                "overallScore",
                                "resultCategory",
                                "frequency",
                                "amplitude",
                                "deviationFromBaseline",
                                "testDuration",
                                "averageSpeed",
                                "mean",
                                "std"),
                        String.join(",",
                                value(result.getId()),
                                value(result.getUserId()),
                                value(result.getTestType()),
                                value(result.getPerformedAt()),
                                value(result.getOverallScore()),
                                value(result.getResultCategory()),
                                value(result.getFrequency()),
                                value(result.getAmplitude()),
                                value(result.getDeviationFromBaseline()),
                                value(result.getTestDuration()),
                                value(result.getAverageSpeed()),
                                value(result.getMean()),
                                value(result.getStd()))
                );

                Files.write(csvFile, lines, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            }
            return csvFile.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to write CSV: " + e.getMessage(), e);
        }
    }

    private Path resolveBasePath() {
        Path configured = Paths.get(exportBaseDir);
        if (!configured.isAbsolute()) {
            configured = Paths.get("").toAbsolutePath().resolve(configured).normalize();
        }
        return configured;
    }

    private String writePng(TestResult result, org.springframework.web.multipart.MultipartFile imageFile) {
        Path basePath = resolveBasePath();
        Path exportDir = basePath.resolve(exportDirName);
        try {
            Files.createDirectories(exportDir);
            String testType = result.getTestType() != null ? result.getTestType() : "unknown";
            String fileName = String.format(
                    "%s_%s_%d.png",
                    testType,
                    FILE_TS.format(result.getPerformedAt()),
                    result.getId());
            Path pngFile = exportDir.resolve(fileName);

            if (Files.exists(pngFile)) {
                pngFile = exportDir.resolve(fileName.replace(".png", "") + "_" + UUID.randomUUID() + ".png");
            }

            Files.write(pngFile, imageFile.getBytes(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            return pngFile.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to write PNG: " + e.getMessage(), e);
        }
    }

    private String value(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}


