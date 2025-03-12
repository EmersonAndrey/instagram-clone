package br.edu.ifpb.instagram.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    String token;

    @BeforeEach
    void generat_ingValid_authentication_before_testing(){
        when(authentication.getName()).thenReturn("usuarioTeste");
        token =jwtUtils.generateToken(authentication);
    }

    @Test
    void test_generate_token(){
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void test_check_validate_token(){
        assertTrue(jwtUtils.validateToken(token));
    }
    @Test
    void test_invalid_token(){
        assertFalse(jwtUtils.validateToken(""));
    }

    @Test
    void test_get_user_name_from_token(){
        String userNameToken = jwtUtils.getUsernameFromToken(token);

        assertEquals(authentication.getName(),userNameToken, "Os userNames devem ser iguais!");
    }

    @Test
    void test_get_invalid_username_from_token(){
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getName()).thenReturn("usuarioTeste02");

        String userNameToken = jwtUtils.getUsernameFromToken(token);

        assertNotEquals(authenticationMock.getName(),userNameToken, "Os userNames devem ser diferentes!");
    }
}
