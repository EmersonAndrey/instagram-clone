package br.edu.ifpb.instagram.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;

@SpringBootTest
public class UserServiceImplTest {

    @MockitoBean
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService; 

    @Test
    void givenValidUserId_whenFindById_thenReturnUserDto() {
        Long userId = 1L;

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(userId);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));

        UserDto userDto = userService.findById(userId);

        assertNotNull(userDto);
        assertEquals(mockUserEntity.getId(), userDto.id());
        assertEquals(mockUserEntity.getFullName(), userDto.fullName());
        assertEquals(mockUserEntity.getEmail(), userDto.email());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void givenNonExistentUserId_whenFindById_thenThrowUserNotFoundException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void givenUserList_whenFindAll_thenReturnListOfUserDtos() {
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

        when(userRepository.findAll()).thenReturn(mockUserEntities);

        List<UserDto> result = userService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertEquals(mockUserEntities.size(), result.size());

        for (int i = 0; i < mockUserEntities.size(); i++) {
            UserEntity entity = mockUserEntities.get(i);
            UserDto dto = result.get(i);

            assertEquals(entity.getId(), dto.id());
            assertEquals(entity.getFullName(), dto.fullName());
            assertEquals(entity.getUsername(), dto.username());
            assertEquals(entity.getEmail(), dto.email());
            assertEquals(entity.getEncryptedPassword(), dto.encryptedPassword());
        }

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void givenEmptyUserList_whenFindAll_thenThrowUsersNotFoundException() {

        when(userRepository.findAll()).thenReturn(Arrays.asList());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.findAll());

        assertEquals("Users not found", exception.getMessage());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void givenUserDtoWithEmptyPassword_whenUpdateUser_thenUserIsUpdatedWithoutPassword() {

        UserDto userDto = new UserDto(1L, "Joao Update", "joaoupdate", "joaoupdate@example.com", "", null);

        when(userRepository.updatePartialUser(userDto.fullName(), userDto.email(), userDto.username(), null,
                userDto.id())).thenReturn(1);

        UserDto updatedUser = userService.updateUser(userDto);

        assertNotNull(updatedUser);
        assertNull(updatedUser.encryptedPassword());

        verify(userRepository, times(1)).updatePartialUser(
                userDto.fullName(), userDto.email(), userDto.username(), null, userDto.id());
    }

    @Test
    void givenUserDtoWithPassword_whenUpdateUser_thenUserIsUpdatedWithNewPassword() {

        UserDto userDto = new UserDto(1L, "Joao Updated", "joaoupdated", "joaoupdated@example.com", null,
                "newpassword");

        when(userRepository.updatePartialUser(any(), any(), any(), any(), any())).thenReturn(1);

        UserDto result = userService.updateUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.fullName(), result.fullName());
        verify(userRepository, times(1)).updatePartialUser(any(), any(), any(), any(), any());
    }

    @Test
    void givenUserDtoWithoutPassword_whenUpdateUser_thenUserIsUpdatedWithoutPassword() {

        UserDto userDto = new UserDto(1L, "Joao Update", "joaoupdate", "joaoupdate@example.com", null, null);

        when(userRepository.updatePartialUser(any(), any(), any(), isNull(), any())).thenReturn(1);
        
        UserDto result = userService.updateUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.fullName(), result.fullName());
        verify(userRepository, times(1)).updatePartialUser(any(), any(), any(), isNull(), any());
    }

    @Test
    void givenNonExistentUserId_whenUpdateUser_thenThrowUserNotFoundException() {

        UserDto userDto = new UserDto(999L, "UserNotExist", "usernotExist", "notexist@example.com", null, "password");
        
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

        doThrow(new RuntimeException("User not found")).when(userRepository).deleteById(999L);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(999L));

        assertEquals("User not found", exception.getMessage());

    }

}
