package com.claude.springboot.app.security.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String mensaje;
    private String code;
    private LocalDateTime timestamp;


    public ErrorResponse(String message, LocalDateTime timestamp) {
        this.code = code;
        this.timestamp = timestamp;
    }
}
