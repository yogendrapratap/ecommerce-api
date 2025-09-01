package com.ecommerceapi.ecommerceapi.service;

import com.ecommerceapi.ecommerceapi.dto.UserDTO;
import com.ecommerceapi.ecommerceapi.dto.UserListDTO;
import com.ecommerceapi.ecommerceapi.entity.User;
import com.ecommerceapi.ecommerceapi.exception.ECommerceAPIValidationException;
import com.ecommerceapi.ecommerceapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        modelMapper = mock(ModelMapper.class);
        userService = new UserService();
        userService.userRepository = userRepository;
        userService.modelMapper = modelMapper;
    }

    @Test
    void testGetUsersDetails() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("john");

        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setFirstName("john");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(dto);

        UserListDTO result = userService.getUsersDetails();

        assertNotNull(result);
        assertEquals(1, result.getUsers().size());
        assertEquals("john", result.getUsers().get(0).getFirstName());
    }

    @Test
    void testUserRegistration() {
        UserDTO input = new UserDTO();
        input.setFirstName("john");

        User entity = new User();
        entity.setFirstName("john");

        User saved = new User();
        saved.setId(1L);
        saved.setFirstName("john");

        UserDTO output = new UserDTO();
        output.setId(1L);
        output.setFirstName("john");

        when(modelMapper.map(input, User.class)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(modelMapper.map(saved, UserDTO.class)).thenReturn(output);

        UserDTO result = userService.userRegistration(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john", result.getFirstName());
    }

    @Test
    void testLogin_success() {
        String username = "john";
        String password = "pass123";

        User mockUser = new User();
        mockUser.setFirstName(username);
        mockUser.setPassword(password);

        UserDTO mappedDto = new UserDTO();
        mappedDto.setFirstName(username);

        when(userRepository.findByUsernameAndPassword(username, password)).thenReturn(mockUser);
        when(modelMapper.map(mockUser, UserDTO.class)).thenReturn(mappedDto);

        UserDTO result = userService.login(username, password);
        assertEquals("john", result.getFirstName());
    }

    @Test
    void testLogin_failure_throwsException() {
        when(userRepository.findByUsernameAndPassword("baduser", "badpass")).thenReturn(null);

        ECommerceAPIValidationException ex = assertThrows(ECommerceAPIValidationException.class,
                () -> userService.login("baduser", "badpass"));

        assertEquals("Login failed, Invalid username or password", ex.getMessage());
    }

    @Test
    void testGetUserDetailsById_valid() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("john");

        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setFirstName("john");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(dto);

        UserDTO result = userService.getUsersDetailsById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john", result.getFirstName());
    }

    @Test
    void testGetUserDetailsById_invalid_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ECommerceAPIValidationException ex = assertThrows(ECommerceAPIValidationException.class,
                () -> userService.getUsersDetailsById(999L));

        assertEquals("Invalid User/User not found", ex.getMessage());
    }
}

