package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserDTO create(UserCreateDTO createData) {
        String email = createData.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email " + email + " already exists");
        }
        User user = userMapper.map(createData);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO findById(Long id) {
        return userMapper.map(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public List<UserDTO> findAll() {
        return userMapper.map(userRepository.findAll());
    }

    public UserDTO update(UserUpdateDTO updateData, Long id) {
        User updatingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userMapper.update(updateData, updatingUser);
        userRepository.save(updatingUser);
        return userMapper.map(updatingUser);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
