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

    private String token;

    @BeforeEach
    void givenBeforeTesting_whenIngValidAuthentication_thenGeneratToken(){
        when(authentication.getName()).thenReturn("usuarioTeste");

        token =jwtUtils.generateToken(authentication);
    }

    @Test
    void givenTest_whenGenerateToken_thenCheck(){
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void givenTest_whenToken_Thencheckvalidate(){ //Verifica a validade do token
        assertTrue(jwtUtils.validateToken(token));
    }
    @Test
    void givenTest_whenReceiveTokenInvalid_thenReturnFalse(){ //verifica a validade de um token invalido
        assertFalse(jwtUtils.validateToken(""));
    }

    @Test
    void givenTest_whenReceiveToken_thenGetUsername(){
        String userNameToken = jwtUtils.getUsernameFromToken(token);

        assertEquals(authentication.getName(),userNameToken, "Os userNames devem ser iguais!");
    }
    @Test
    void givenTest_whenReceiveToken_thenValidUsername(){
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getName()).thenReturn("usuarioTeste02");

        String userNameToken = jwtUtils.getUsernameFromToken(token);

        assertNotEquals(authenticationMock.getName(),userNameToken, "Os userNames devem ser diferentes!");
    }
}
