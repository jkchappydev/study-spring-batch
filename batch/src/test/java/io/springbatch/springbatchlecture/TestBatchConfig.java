package io.springbatch.springbatchlecture;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

// 테스트용 Batch 인프라 활성화
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class TestBatchConfig {
}

