package com.ticketing.authservice.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private String FullName;
    private String username;
    private String email;
    private String password;


}
