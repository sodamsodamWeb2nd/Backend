package com.example.sodamsodam;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SodamsodamApplication {

    public static void main(String[] args) {
        // .env 파일 로드
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // .env 파일이 없어도 에러 발생하지 않음
                .load();

        // .env의 값들을 시스템 프로퍼티로 설정
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(SodamsodamApplication.class, args);
    }

}