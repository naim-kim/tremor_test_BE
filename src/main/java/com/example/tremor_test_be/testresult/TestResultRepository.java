package com.example.tremor_test_be.testresult;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    List<TestResult> findByUserIdOrderByPerformedAtDesc(Long userId);

    List<TestResult> findByUserIdAndTestTypeOrderByPerformedAtDesc(Long userId, String testType);
}

