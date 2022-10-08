package com.shz.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;

@Data
@ToString
@Configuration
public class JwtProperties {
    private String header="Authorization";

    private String base64Secret="shzzz";

    private Long tokenValidityInSeconds=14400000L;
}
