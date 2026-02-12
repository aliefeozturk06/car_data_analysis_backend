package com.infodif.car_data_analysis;

import com.infodif.car_data_analysis.repository.CarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
@Slf4j
public class CarDataAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarDataAnalysisApplication.class, args);
    }

    @Bean
    public CommandLineRunner debugCarCount(CarRepository carRepository) {
        return args -> {
            long count = carRepository.count();
            log.info("=====================================");
            log.info("DATABASE CONNECTION CONTROL:");
            log.info("Actively Seen Car Count: {}", count);
            log.info("=====================================");
        };
    }
}