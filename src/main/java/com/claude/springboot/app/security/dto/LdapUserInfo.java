package com.claude.springboot.app.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LdapUserInfo {
    private String username;
    private String nombres;
    private String apellidos;
    private String email;
}
