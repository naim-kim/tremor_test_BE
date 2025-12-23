package com.example.tremor_test_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TremorTestBeApplication {

    public static void main(String[] args) {
        // Set system property for Apache Commons FileUpload file count limit before Spring Boot starts
        // This prevents FileCountLimitExceededException when uploading multipart requests
        System.setProperty("org.apache.tomcat.util.http.fileupload.FileCountLimit", "100");
        
        SpringApplication.run(TremorTestBeApplication.class, args);
    }

}
