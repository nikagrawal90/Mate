package com.mate.service;

import com.mate.entity.UserEntity;
import com.mate.exception.UserNotFoundException;
import com.mate.model.dto.UserDto;
import com.mate.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDto registerUser(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDto, userEntity);

        System.out.println("Before saving user entity: " + userEntity);
        userRepository.save(userEntity);
        System.out.println("After saving user entity: " + userEntity);

        userDto.setId(userEntity.getUserId());
        return userDto;
    }

    public List<UserDto> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        userEntities.forEach(userEntity -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            userDtos.add(userDto);
        });

        return userDtos;
    }

    public UserEntity getUserById(String userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User=%s not found", userId)));
    }

    public void deleteUser(String userId) throws UserNotFoundException {
        if(userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException(String.format("User=%s not found", userId));
        }
    }
}
