package com.example.sodamsodam.apps.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "테스트용 API")
public class TestController {

    @Operation(summary = "헬로 월드", description = "서버가 정상 작동하는지 확인하는 테스트 API")
    @GetMapping("/hello")
    public String hello() {
        return "Hello, SodamSodam!";
    }

    @Operation(summary = "상태 확인", description = "서버 상태를 확인합니다")
    @GetMapping("/status")
    public String status() {
        return "서버가 정상적으로 실행 중입니다!";
    }
}