package com.microservice.publicacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationTokenResponse {

    Integer userId;
    String email;
    Boolean isValid;

}




