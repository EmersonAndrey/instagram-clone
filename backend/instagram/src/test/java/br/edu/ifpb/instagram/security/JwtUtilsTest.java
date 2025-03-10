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
    void generatingValidAuthenticationBeforeTesting(){
        when(authentication.getName()).thenReturn("usuarioTeste");
        token =jwtUtils.generateToken(authentication);
    }

    @Test
    void testGenerateToken(){
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testCheckValidateToken(){
        assertTrue(jwtUtils.validateToken(token));
    }
    @Test
    void testInvalidToken(){
        assertFalse(jwtUtils.validateToken(""));
    }

    @Test
    void testGetUserNameFromToken(){
        String userNameToken = jwtUtils.getUsernameFromToken(token);

        assertEquals(authentication.getName(),userNameToken, "Os userNames devem ser iguais!");
    }

    @Test
    void testGetInvalidUsernameFromToken(){
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getName()).thenReturn("usuarioTeste02");

        String userNameToken = jwtUtils.getUsernameFromToken(token);

        assertNotEquals(authenticationMock.getName(),userNameToken, "Os userNames devem ser diferentes!");
    }
}
