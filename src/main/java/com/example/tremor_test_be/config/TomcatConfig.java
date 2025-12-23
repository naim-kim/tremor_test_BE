package com.example.tremor_test_be.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.servlet.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        
        factory.addConnectorCustomizers(connector -> {
            // Increase multipart limits
            connector.setProperty("maxPostSize", "20971520"); // 20MB
            connector.setProperty("maxParameterCount", "1000");
            connector.setProperty("maxSwallowSize", "20971520"); // 20MB
        });
        
        return factory;
    }
}

