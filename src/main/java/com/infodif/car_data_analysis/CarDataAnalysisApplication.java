package com.infodif.car_data_analysis;

import com.infodif.car_data_analysis.repository.CarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class CarDataAnalysisApplication {

    static void main(String[] args) {
        SpringApplication.run(CarDataAnalysisApplication.class, args);
    }
    @Bean
    public CommandLineRunner debugCarCount(CarRepository carRepository) {
        return args -> {
            long count = carRepository.count();
            System.out.println("=====================================");
            System.out.println("DATABASE CONNECTION CONTROL:");
            System.out.println("Actively Seen Car Count: " + count);
            System.out.println("=====================================");
        };
    }
}
