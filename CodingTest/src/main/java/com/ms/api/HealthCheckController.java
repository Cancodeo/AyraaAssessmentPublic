package com.ms.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/heartBeat")
    public String heartBeat() {
        return "Integration App is running smoothly ...!!!";
    }
}
