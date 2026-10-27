package com.bigobooks.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class SecurityConfigTest {

	@Test
    void mapsAdminRoleClaimToSpringRoleAuthority() {
		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "HS512")
                .claim("role", "ADMIN")
				.build();

		var authentication = new SecurityConfig().jwtAuthenticationConverter("role", "ROLE_").convert(jwt);

        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
	}
}
