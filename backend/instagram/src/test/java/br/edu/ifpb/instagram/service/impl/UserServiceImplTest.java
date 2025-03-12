package br.edu.ifpb.instagram.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;

@SpringBootTest
public class UserServiceImplTest {

    @MockitoBean
    UserRepository userRepository; // Repositório simulado

    @Autowired
    UserServiceImpl userService; // Classe sob teste

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @Test
    void testFindById_ReturnsUserDto() {
        // Configurar o comportamento do mock
        Long userId = 1L;

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(userId);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));

        // Executar o método a ser testado
        UserDto userDto = userService.findById(userId);

        // Verificar o resultado
        assertNotNull(userDto);
        assertEquals(mockUserEntity.getId(), userDto.id());
        assertEquals(mockUserEntity.getFullName(), userDto.fullName());
        assertEquals(mockUserEntity.getEmail(), userDto.email());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_ThrowsExceptionWhenUserNotFound() {
        // Configurar o comportamento do mock
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Executar e verificar a exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found", exception.getMessage());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void givenUserList_whenFindAll_thenReturnListOfUserDtos() {
        // Cria uma lista de UserEntity simulada
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setFullName("Joao Silva");
        user1.setUsername("joaosilva");
        user1.setEmail("joao@example.com");
        user1.setEncryptedPassword("encryptedPassword1");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setFullName("Maria Souza");
        user2.setUsername("mariasouza");
        user2.setEmail("maria@example.com");
        user2.setEncryptedPassword("encryptedPassword2");

        List<UserEntity> mockUserEntities = Arrays.asList(user1, user2);

        // Mock
        when(userRepository.findAll()).thenReturn(mockUserEntities);

        // Chama o método a ser testado
        List<UserDto> result = userService.findAll();

        // Verifica se a lista de DTOs não está vazia
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verifica se o tamanho da lista de DTOs é o mesmo da lista de entidades
        assertEquals(mockUserEntities.size(), result.size());

        // Verifica se os dados de cada DTO estão corretos
        for (int i = 0; i < mockUserEntities.size(); i++) {
            UserEntity entity = mockUserEntities.get(i);
            UserDto dto = result.get(i);

            assertEquals(entity.getId(), dto.id());
            assertEquals(entity.getFullName(), dto.fullName());
            assertEquals(entity.getUsername(), dto.username());
            assertEquals(entity.getEmail(), dto.email());
            assertEquals(entity.getEncryptedPassword(), dto.encryptedPassword());
        }

        // Verifica se o método do repositório foi chamado corretamente
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void givenEmptyUserList_whenFindAll_thenThrowUsersNotFoundException() {

        //Configura
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        //Captura exceçao
        Exception exception = assertThrows(RuntimeException.class, () -> userService.findAll());

        //Verificaçoes
        assertEquals("Users not found", exception.getMessage());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void givenUserDtoWithEmptyPassword_whenUpdateUser_thenUserIsUpdatedWithoutPassword() {

        // Configura
        UserDto userDto = new UserDto(1L, "Joao Update", "joaoupdate", "joaoupdate@example.com", "", null);

        // Mock do comportamento do repositório simulando 1 linha modificada
        when(userRepository.updatePartialUser(userDto.fullName(), userDto.email(), userDto.username(), null,
                userDto.id())).thenReturn(1);

        // Chama o método testado
        UserDto updatedUser = userService.updateUser(userDto);

        // Verificaçoes
        assertNotNull(updatedUser);
        assertNull(updatedUser.encryptedPassword());

        // Verifica se o método de update foi chamado corretamente
        verify(userRepository, times(1)).updatePartialUser(
                userDto.fullName(), userDto.email(), userDto.username(), null, userDto.id());
    }

    @Test
    void givenUserDtoWithPassword_whenUpdateUser_thenUserIsUpdatedWithNewPassword() {

        //Configura
        UserDto userDto = new UserDto(1L, "Joao Updated", "joaoupdated", "joaoupdated@example.com", null,
                "newpassword");

        // Mock
        when(userRepository.updatePartialUser(any(), any(), any(), any(), any())).thenReturn(1);

        // Chama o método testado
        UserDto result = userService.updateUser(userDto);

        // Verificações
        assertNotNull(result);
        assertEquals(userDto.fullName(), result.fullName());
        verify(userRepository, times(1)).updatePartialUser(any(), any(), any(), any(), any());
    }

    @Test
    void givenUserDtoWithoutPassword_whenUpdateUser_thenUserIsUpdatedWithoutPassword() {

        //Configura
        UserDto userDto = new UserDto(1L, "Joao Update", "joaoupdate", "joaoupdate@example.com", null, null);

        //Mock
        when(userRepository.updatePartialUser(any(), any(), any(), isNull(), any())).thenReturn(1);
        
        //Chama o metodo
        UserDto result = userService.updateUser(userDto);

        //Verificaçoes
        assertNotNull(result);
        assertEquals(userDto.fullName(), result.fullName());
        verify(userRepository, times(1)).updatePartialUser(any(), any(), any(), isNull(), any());
    }

    @Test
    void givenNonExistentUserId_whenUpdateUser_thenThrowUserNotFoundException() {

        //Configura
        UserDto userDto = new UserDto(999L, "UserNotExist", "usernotExist", "notexist@example.com", null, "password");
        
        //Mock
        when(userRepository.updatePartialUser(any(), any(), any(), any(), any())).thenReturn(0);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userDto));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).updatePartialUser(any(), any(), any(), any(), any());
    }

    @Test
    void givenValidUserId_whenDeleteUser_thenUserIsDeleted() {

        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenNonExistentUserId_whenDeleteUser_thenThrowUserNotFoundException() {

        //Configura o metodo para lançar exceção
        doThrow(new RuntimeException("User not found")).when(userRepository).deleteById(999L);

        //Captura a exceção quando o metodo é chamdo
        Exception exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(999L));

        //Verificaçao
        assertEquals("User not found", exception.getMessage());

    }

}
